# jblinktree

It should be easy to use b-link tree implementations.
B-link tree is thread safe map. 
Implementations is based on "Efficient Locking for Concurrent Operations on B-Trees" 
written by Philip L. Lehman and S. Bing Yao at 1981.

## TODO
* junit test proving that tree is thread safe
* key should be any comparable data type with fixed length.
* value should by any data type with fixed length 4 bytes. 
* there should be run time message that L parameter 1 is forbidden and 2 is discourage.
* node locks should be implemented in store not at node
* tree should support some LRU cache of nodes  