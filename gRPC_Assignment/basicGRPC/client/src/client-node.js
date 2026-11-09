
const PROTO_PATH = __dirname + '/../../protos/keysearch/proto/keysearch.proto';
const SOCKET = 'localhost:50051';

const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');

const options = {
    keepCase: true,
    longs: String,
    enums: String,
    defaults: true,
    oneofs: true,
};

const packageDefinition = protoLoader.loadSync(PROTO_PATH, options);
const keysearchProto = grpc.loadPackageDefinition(packageDefinition).proto.keysearch;

function main() {
    const args = process.argv.slice(2);
    const command = args[0];
    const word = args[1];

    // Validate command-line arguments
    if (
        (command !== 'whohas' && command !== 'whohas2') ||
        !word ||
        (command === 'whohas' && args.length !== 2) ||
        (command === 'whohas2' && args.length !== 3)
    ) {
        console.log('Usage:');
        console.log('  make client-node ARGS="whohas <word>"');
        console.log('  make client-node ARGS="whohas2 <word> <mincount>"');
        process.exit(1);
    }

    const client = new keysearchProto.KeywordSearch(
        SOCKET,
        grpc.credentials.createInsecure()
    );

    if (command === 'whohas') {
        client.Whohas({ word: word }, (err, response) => {
            if (err) {
                console.error('gRPC error:', err.message);
                process.exit(1);
            }

            console.log(response.Results);
            process.exit(0);
        });
    } else {
        const mincount = Number(args[2]);

        if (!Number.isSafeInteger(mincount) || mincount < 1 || mincount > 2147483647) {
            console.error('Error: mincount must be a positive 32-bit integer.');
            process.exit(1);
        }

        client.Whohas2({ word: word, mincount: mincount }, (err, response) => {
            if (err) {
                console.error('gRPC error:', err.message);
                process.exit(1);
            }

            console.log(response.Results);
            process.exit(0);
        });
    }
}

main();
