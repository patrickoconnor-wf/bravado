from utils.frugal_type_resolver import FrugalTypeResolver
from utils.logging import get_logger

_logger = get_logger('generators.StructGenerator')


class StructGenerator(object):

    @staticmethod
    def generate(name: str, attributes: dict) -> str:
        _logger.debug(f'Generating struct for `{name}`')
        struct = StructGenerator._generate_header(name)
        # There are multiple ways for a struct to get properties:
        #   - the `parameters` tag
        #   - referenced using the `allOf` tag
        #   - the `properties` tag
        combined_properties = {}
        parameters: list = attributes.get('parameters', [])
        for parameter in parameters:
            combined_properties[parameter['name']] = parameter
        all_of: list = attributes.get('allOf', [])
        for propMap in all_of:
            combined_properties.update(propMap['properties'])
        properties: dict = attributes.get('properties', {})
        combined_properties.update(properties)

        assert combined_properties, f'Missing properties for {name}'
        index = 0
        for name, spec in combined_properties.items():
            index += 1
            struct += StructGenerator._generate_property(name, spec, index)
        struct += StructGenerator._generate_footer()
        return struct

    @staticmethod
    def _generate_header(name: str) -> str:
        return f'struct {name} {{\n'

    @staticmethod
    def _generate_footer() -> str:
        return '}\n'

    @staticmethod
    def _generate_property(name: str, prop: dict, index: int) -> str:
        return f'    {index}: {FrugalTypeResolver.resolve(name, prop)} {name}\n'

    @staticmethod
    def generate_request(operation: dict):
        request_name = operation.get('operationId')
        assert request_name, 'Missing operationId'
        return StructGenerator.generate(f'{request_name}Request', operation)
