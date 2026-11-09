import logging
import os
from concurrent import futures

import grpc
from grpc_reflection.v1alpha import reflection
from keysearch.proto import keysearch_pb2_grpc, keysearch_pb2
from typing import Text


fakedatabase={} #assume this is a database of index files...
def popdatabase(filepath):
    if(not os.path.exists(filepath)):
        logging.exception("index file not found")
    with open(filepath, "r") as f:
        for line in f:
            lsplit=line.strip().split("|.|")
            filename=lsplit[0]
            fakedatabase[filename]=[]
            wordcount=int((len(lsplit)-1)/2)
            #print(f"{filename}:{wordcount}")
            for i in range(wordcount):
                fakedatabase[filename].append( (lsplit[(i*2)+1],int(lsplit[(i+1)*2])) )
    #print(fakedatabase)


class Server(keysearch_pb2_grpc.KeywordSearchServicer):

    def search(self, queryword, mincount):
        response = keysearch_pb2.RepeatedResult()

        if queryword in fakedatabase:
            for filename, i_count in fakedatabase[queryword]:
                if i_count >= mincount:
                    response.Results.append(
                        keysearch_pb2.Result(
                            word=queryword,
                            file=filename,
                            count=i_count
                        )
                    )

        return response

    def Whohas(self, request, context):
        return self.search(request.word, 1)

    def Whohas2(self, request, context):
        if request.mincount < 1:
            context.abort(
                grpc.StatusCode.INVALID_ARGUMENT,
                "Minimum count must be at least 1"
            )

        return self.search(request.word, request.mincount)

        
def _serve(port):
    bind_address = f"[::]:{port}"

    server = grpc.server(futures.ThreadPoolExecutor())
    keysearch_pb2_grpc.add_KeywordSearchServicer_to_server(
        Server(), server
    )

    server.add_insecure_port(bind_address)
    server.start()

    logging.info("Listening on %s.", bind_address)
    server.wait_for_termination()


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)

    _PORT = os.environ["SERVER_GRPC_PORT"]
    print(f"PORT={_PORT}")

    popdatabase("./server/db/index.txt")
    _serve(_PORT)

