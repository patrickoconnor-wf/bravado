from generators.generator import Generator
from generators.struct_generator import StructGenerator
from utils.logging import get_logger

_logger = get_logger('frugal_generator.FrugalGenerator')


class FrugalGenerator(Generator):
    def __init__(self, spec: dict) -> None:
        super().__init__()
        self.spec = spec

    def generate(self):
        definitions: dict = self.spec.get('definitions')
        for name, spec in definitions.items():
            _logger.debug(StructGenerator(name, spec).generate())
