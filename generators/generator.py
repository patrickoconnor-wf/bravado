import abc


class Generator(abc.ABC):
    @abc.abstractmethod
    def generate(self):
        pass
