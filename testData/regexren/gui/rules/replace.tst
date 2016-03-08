# Test format:
#	<original_name>;<text_to_replace>;<text_to_insert>;<from_index>;<to_index>;<target>;<match_case>;<regex>;<result_name>
#
# As target, please use values of static fields of ReplaceFactory.
# Indexes starts from 0. 
# <match_case> and <regex> are boolean values: to indicate true insert 1, other values will be interpreted as false.

testTestFileName.ext;t;Z;0;100;0;0;0;ZesZZesZFileName.ext
testTestFileName.ext;t;Z;0;005;0;0;0;ZesZZestFileName.ext
testTestFileName.ext;t;Z;0;100;0;1;0;ZesZTesZFileName.ext
testTestFileName.ext;t;Z;0;005;0;1;0;ZesZTestFileName.ext
te.mp1. (co.py).brr;t;;0;100;0;0;0;e.mp1. (co.py).brr