# jblinktree

It should be easy to use b-link tree implementations.
B-link tree is thread safe map. 
Implementations is based on "Efficient Locking for Concurrent Operations on B-Trees" 
written by Philip L. Lehman and S. Bing Yao at 1981.

## TODO
* make L custom parameter with some default value
* junit test proving that tree is thread safe
* key should be any comparable data type with fixed length.
* value should by any data type with fixed length 4 bytes. 
* there should be run time message that L parameter 1 is forbidden and 2 is discourage.
* node locks should be implemented in store not at node
* add some node persisting to disk
* tree should support some LRU cache of nodes
* documentations
* user guide
* delete operation
* contains operation
* leaf node maybe could hold one key,value pair more in P0 and max value places.
