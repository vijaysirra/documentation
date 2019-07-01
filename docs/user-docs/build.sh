gitbook build

chmod -R 777 _book/

cd _book

tar -cJf _book.tar.xz *

cd ../
mv _book/_book.tar.xz _book.tar.xz
