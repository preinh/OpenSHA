#!/bin/bash
mkdir -p lib
pushd lib
wget -r -np -nd -Ajar http://gemsun02.ethz.ch/~jmckenty/javalibs/
popd
