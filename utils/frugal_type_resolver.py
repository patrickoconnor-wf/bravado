from utils.logging import get_logger

_logger = get_logger('utils.FrugalTypeResolver')


class InvalidTypeException(Exception):
    pass


class FrugalTypeResolver(object):
    INTEGER_MAP = {
        'int32': 'i32',
        'int64': 'i64'
    }

    @staticmethod
    def resolve(name: str, prop: dict) -> str:
        prop_type = prop.get('type')
        assert prop_type, f'Missing type for {name}'
        if prop_type == 'integer':
            prop_format = prop.get('format')
            return FrugalTypeResolver.INTEGER_MAP[
                prop_format] if prop_format else FrugalTypeResolver.INTEGER_MAP[
                'int32']
        elif prop_type == 'number':
            return 'double'
        elif prop_type == 'string':
            return 'string'
        elif prop_type == 'boolean':
            return 'bool'
        elif prop_type == 'array':
            return FrugalTypeResolver.resolve_array(prop)
        elif prop_type == 'object':
            return name
        else:
            raise InvalidTypeException(
                f'Type `{prop_type}` is not a valid swagger type')

    @staticmethod
    def resolve_array(prop) -> str:
        # TODO Check for `uniqueItems: true` and use a set
        # XXX: I'm pretty sure passing "items" here is a bug but I don't have
        # tests to confirm.
        return f'list<{FrugalTypeResolver.resolve("items", prop.get("items"))}>'
