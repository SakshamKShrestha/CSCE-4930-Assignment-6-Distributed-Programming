"""The Python implementation of the GRPC client."""

from __future__ import print_function
import logging
import sys
import os
from concurrent import futures

import grpc
from grpc_reflection.v1alpha import reflection
from keysearch.proto import keysearch_pb2
from typing import Text
from keysearch.proto.keysearch_pb2_grpc import KeywordSearchStub

def get_fileindex(stub: KeywordSearchStub, theword: str) -> keysearch_pb2.RepeatedResult:
    return stub.Whohas(
        keysearch_pb2.Query(word=theword)
    )
def anotherfunction(stub: KeywordSearchStub, sourcedId: str) -> keysearch_pb2.RepeatedResult:
    return None


getters: dict = {
    'whohas': get_fileindex,
    'anotherservice':anotherfunction
}


def run(getter: str, word: str):
    #credentials = grpc.ssl_channel_credentials()

    with grpc.insecure_channel('localhost:50051') as channel:
        stub = KeywordSearchStub(channel)
        response = getters[getter](stub, word)
        print(f"response:\n{response}")

#example : make client
if __name__ == '__main__':
    logging.basicConfig()
    print(sys.argv[1])
    run(sys.argv[1], sys.argv[2])
