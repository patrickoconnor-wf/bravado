#!/usr/bin/env python
from pprint import pprint

from prance import ResolvingParser

from generators.frugal_generator import FrugalGenerator


def main():
    parser = ResolvingParser('petstore-expanded.yaml')
    pprint(parser.specification['definitions'])
    generator = FrugalGenerator(parser.specification)
    generator.generate()


if __name__ == '__main__':
    main()
