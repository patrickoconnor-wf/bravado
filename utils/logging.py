import logging

import sys


# TODO make stream and level configurable
def get_logger(name: str):
    logger = logging.getLogger(name)
    logger.setLevel(logging.DEBUG)
    handler = logging.StreamHandler(sys.stdout)
    logger.addHandler(handler)
    return logger
