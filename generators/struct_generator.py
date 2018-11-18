from generators.generator import Generator
from utils.frugal_type_resolver import FrugalTypeResolver
from utils.logging import get_logger

_logger = get_logger('generators.StructGenerator')


class StructGenerator(Generator):

    def __init__(self, name: str, attributes: dict) -> None:
        super().__init__()
        self.name = name
        self.attributes = attributes

    def generate(self) -> str:
        _logger.debug(f'Generating struct for `{self.name}`')
        struct = self._generate_header(self.name)
        # There are multiple ways for a struct to get properties:
        #   - referenced using the `allOf` tag
        #   - the `properties` tag
        combined_properties = {}
        all_of: list = self.attributes.get('allOf', [])
        for propMap in all_of:
            combined_properties.update(propMap['properties'])
        properties: dict = self.attributes.get('properties', {})
        combined_properties.update(properties)

        assert combined_properties, f'Missing properties for {self.name}'
        index = 0
        for name, spec in combined_properties.items():
            index += 1
            struct += self._generate_property(name, spec, index)
        struct += self._generate_footer()
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
