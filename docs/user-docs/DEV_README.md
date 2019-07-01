USER DOCS - DEV SETUP

With docker:-

To run user docs in local using docker, inside user docs folder run the command

To start server:
docker run --rm -v "$PWD:/gitbook" -p 4000:4000 billryan/gitbook gitbook serve 

To build:
docker run --rm -v "$PWD:/gitbook" -p 4000:4000 billryan/gitbook gitbook build

Without docker:

Install gitbook-cli using npm:

 npm install -g gitbook-cli

Then, inside user docs folder run the command,

To start server:
gitbook serve
To build:
gitbook build

