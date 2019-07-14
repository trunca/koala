#!/bin/sh
echo ""
echo ""
echo "Stage 1: git pull for new changes"
echo ""
cd meta-hypercube
git checkout develop
git pull
cd ..
cd meta-dream
git checkout develop
git pull
cd ..
cd meta-fulan
git checkout develop
git pull
cd ..
cd meta-octagon
git checkout develop
git pull
cd ..
cd meta-xcore
git checkout develop
git pull
cd ..
echo "Stage 2: git add for new changes"
echo ""
git add *
git commit -S -m "Update submodules"
echo "Stage 3: git push for new changes"
echo ""
git push
echo "Done: all git repositories get updated to their latest versions!"
echo ""
