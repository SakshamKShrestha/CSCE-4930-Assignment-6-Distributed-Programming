### These instructions were made with a Linux machine in mind

### Common prerequisites:
python (v3.8+), pyenv, pipenv, protobuf, Node.js (v10.0+), grpc, npm (v6.0+)

### Always a good idea to update package manager:
```
$ apt update
$ apt upgrade
```

### How to install python:
```
$ apt install python3
```

### How to install and update pip:
```
$ apt install python3-pip
$ python3 -m pip install --upgrade pip
```

### How to install pipenv:
```
$ pip3 install pipx
$ pip3 install pipenv
```

### How to install protobuf:
```
$ pip3 install protobuf
```

### How to install grpc:
```
$ pip3 install grpcio-tools
```

### How to install Node.js:
```
$ apt install nodejs
```

### How to install and update npm:
```
$ apt install npm
$ npm install -g npm@next
```

### Now all of the immediate dependencies should be installed

### go to basicGRPC directory
```
$ cd basicGRPC
```

### Generate the preliminary libraries and build the virtual environment
```
$ make prelim
```

### start python env (need to do everytime)
### If the following does not work, try Pipenv shell with an uppercase P
```
$ pipenv shell
```

### Instantiate the environment variables. These are constants that are important to the application
```
$ . ./server.env
```

### Make and run server
```
$ make server
```

### The server is now running
### Do not close this terminal

### Now it's time to make and run the client
### Open up a new terminal (be sure to enter the virtual env again with pipenv shell)
### ARGS= is a way to pass along command line arguments from make file to 
### first argument is the grpc function name to call (currently we only have whohas)
### next argument is the argument of function that we call (in this case, keyword to search)
### example of calling whohas through grpc to search for word "money":
```
$ make client-py ARGS="whohas money"
```

### Execute client-node, where money is the keyword we are searching for
```
$ make client-node ARGS="whohas money"
```

### TIPS on implementing the two-argument function:
### You only need to implement the new function in the Node.js version of the client
### A few sections of code can be reimplemented to support the new function
### mincount in server/src/server.py will need to be altered
### Query and KeywordSearch in protos/keysearch/proto/keysearch.proto will need to be altered as well
### The client-node.js file in client/src will also need to be altered
