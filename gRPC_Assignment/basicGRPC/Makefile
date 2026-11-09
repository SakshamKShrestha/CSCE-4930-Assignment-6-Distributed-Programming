.PHONY: help list targets protos dist-server requirements startapp projectid
.DEFAULT_GOAL := help

PROJECT_ID=keywordsearch

ME  := $(realpath $(firstword $(MAKEFILE_LIST)))
PWD := $(dir $(ME))

help: targets ## List targets [aka help | list | targets]
list: targets
targets:
	@echo
	@echo  "Make targets:"
	@echo
	@cat $(ME) | \
	    sed -n -E 's/^([^.[:space:]][^: ]+)\s*:(([^=#]*##\s*(.*[^[:space:]])\s*))$$/    \1	\4/p' | \
	    sort -u | \
	    expand -t20
	@echo


check-server-env:
	@if [ -z "${SERVER_GRPC_PORT}"]; then \
  		echo "!! ERROR: One or more server variables missing:"; \
  		echo " - SERVER_GRPC_PORT"; \
  		echo " - SERVER_BASE_URL"; \
  		echo ""; \
  		echo "Did you source server.env ?"; \
  		exit 1; \
	fi

protos: ## Compile .proto files
	rm -f libs/proto/keyword_search/proto/*.py
	python3 -m grpc_tools.protoc \
	       -I./protos \
	       --include_imports \
	       --include_source_info \
	       --python_out=./libs/proto \
	       --grpc_python_out=./libs/proto \
	       --descriptor_set_out=./keysearch_service.pb \
	       ./protos/keysearch/proto/*.proto

node-libs:
	npm install --prefix "${PWD}./client/src/" @grpc/grpc-js
	npm install --prefix "${PWD}./client/src/" @grpc/proto-loader

prelim: protos
	pipenv install

server: check-server-env protos ## Launch server app locally via pipenv
	pipenv run python3 "${PWD}./server/src/server.py"

client-py: protos ## Launch client app locally via pipenv
	pipenv run python3 "${PWD}./client/src/client-py.py" ${ARGS}

client-node: node-libs
	node "${PWD}./client/src/client-node.js" ${ARGS}