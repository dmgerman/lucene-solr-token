begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SolrDocumentTest
specifier|public
class|class
name|SolrDocumentTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|Float
name|fval
init|=
operator|new
name|Float
argument_list|(
literal|10.01f
argument_list|)
decl_stmt|;
name|Boolean
name|bval
init|=
name|Boolean
operator|.
name|TRUE
decl_stmt|;
name|String
name|sval
init|=
literal|"12qwaszx"
decl_stmt|;
comment|// Set up a simple document
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"b"
argument_list|,
name|bval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"s"
argument_list|,
name|sval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// again, but something else
comment|// make sure we can pull values out of it
name|assertEquals
argument_list|(
name|fval
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fval
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"f"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fval
argument_list|,
operator|(
operator|(
name|Collection
argument_list|<
name|Object
argument_list|>
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"f"
argument_list|)
operator|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bval
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sval
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"f"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"xxxxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"xxxxx"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|doc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
name|keys
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|keys
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|keys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[b, f, s]"
argument_list|,
name|keys
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// set field replaced existing values:
name|doc
operator|.
name|setField
argument_list|(
literal|"f"
argument_list|,
name|fval
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"f"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fval
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"n"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"n"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now remove some fields
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|doc
operator|.
name|removeFields
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|doc
operator|.
name|removeFields
argument_list|(
literal|"asdgsadgas"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"f"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnsupportedStuff
specifier|public
name|void
name|testUnsupportedStuff
parameter_list|()
block|{
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|containsValue
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|entrySet
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|putAll
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|values
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|remove
argument_list|(
literal|"key"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|containsValue
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|entrySet
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|putAll
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|values
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|remove
argument_list|(
literal|"key"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
try|try
block|{
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should be unsupported!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{}
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"aaa"
argument_list|,
literal|"bbb"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bbb"
argument_list|,
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddCollections
specifier|public
name|void
name|testAddCollections
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|c0
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"bbb"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"ccc"
argument_list|)
expr_stmt|;
name|c0
operator|.
name|add
argument_list|(
literal|"ddd"
argument_list|)
expr_stmt|;
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"v"
argument_list|,
name|c0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|c0
operator|.
name|size
argument_list|()
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"v"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|c0
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Same thing with an array
name|Object
index|[]
name|arr
init|=
operator|new
name|Object
index|[]
block|{
literal|"aaa"
block|,
literal|"aaa"
block|,
literal|"aaa"
block|,
literal|10
block|,
literal|'b'
block|}
decl_stmt|;
name|doc
operator|=
operator|new
name|SolrDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"v"
argument_list|,
name|arr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|arr
operator|.
name|length
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"v"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// try the same thing with 'setField'
name|doc
operator|.
name|setField
argument_list|(
literal|"v"
argument_list|,
name|arr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|arr
operator|.
name|length
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"v"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|doc
operator|.
name|getFieldNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterable
name|iter
init|=
operator|new
name|Iterable
argument_list|()
block|{
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
name|c0
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"v"
argument_list|,
name|iter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|c0
operator|.
name|size
argument_list|()
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"v"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// do it again to get twice the size...
name|doc
operator|.
name|addField
argument_list|(
literal|"v"
argument_list|,
name|iter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|c0
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|,
name|doc
operator|.
name|getFieldValues
argument_list|(
literal|"v"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// An empty list:
name|doc
operator|.
name|setField
argument_list|(
literal|"empty"
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"empty"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Try the JSTL accessor functions...
name|assertFalse
argument_list|(
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
literal|"v"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"g"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"g"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|getFieldValueMap
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
literal|"g"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|getFieldValuesMap
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
literal|"g"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDuplicate
specifier|public
name|void
name|testDuplicate
parameter_list|()
block|{
name|Float
name|fval0
init|=
operator|new
name|Float
argument_list|(
literal|10.01f
argument_list|)
decl_stmt|;
name|Float
name|fval1
init|=
operator|new
name|Float
argument_list|(
literal|11.01f
argument_list|)
decl_stmt|;
name|Float
name|fval2
init|=
operator|new
name|Float
argument_list|(
literal|12.01f
argument_list|)
decl_stmt|;
comment|// Set up a simple document
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval0
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval1
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval2
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|(
literal|3
operator|*
literal|5
operator|)
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"f"
argument_list|)
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMapInterface
specifier|public
name|void
name|testMapInterface
parameter_list|()
block|{
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|instanceof
name|Map
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Map
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|SolrDocument
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|indoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|indoc
operator|instanceof
name|Map
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Map
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|indoc
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

