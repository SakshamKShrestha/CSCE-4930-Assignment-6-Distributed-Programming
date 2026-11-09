import setuptools

setuptools.setup(
    name='keysearch-proto',
    packages=['keysearch.proto'],
    install_requires=[
        'grpcio',
        'grpcio-tools',
        'grpcio-testing',
        'grpcio-reflection',
        'googleapis-common-protos',
        'googleapis-common-protos-stubs',
    ]
)
