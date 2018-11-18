from pprint import pprint

from generators.struct_generator import StructGenerator
from utils.logging import get_logger

_logger = get_logger('frugal_generator.FrugalGenerator')


class FrugalGenerator(object):
    def __init__(self, spec: dict) -> None:
        super().__init__()
        self.spec = spec

    def generate(self):
        struct_string = ''
        definitions: dict = self.spec.get('definitions')
        for name, spec in definitions.items():
            struct_string += StructGenerator.generate(name, spec)
        self._generate_structs_for_paths()

    def _generate_structs_for_paths(self):
        paths: dict = self.spec.get('paths')
        pprint(paths)
        for path, operation in paths.items():
            for operation_method, operation_body in operation.items():
                print(StructGenerator.generate_request(operation_body))
