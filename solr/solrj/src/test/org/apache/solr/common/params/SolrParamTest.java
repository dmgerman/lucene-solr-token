begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|ArrayList
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
name|SolrException
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
name|util
operator|.
name|NamedList
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SolrParamTest
specifier|public
class|class
name|SolrParamTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testParamIterators
specifier|public
name|void
name|testParamIterators
parameter_list|()
block|{
name|ModifiableSolrParams
name|aaa
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|aaa
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"a1"
argument_list|)
expr_stmt|;
name|aaa
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"a2"
argument_list|)
expr_stmt|;
name|assertIterSize
argument_list|(
literal|"aaa: foo"
argument_list|,
literal|1
argument_list|,
name|aaa
argument_list|)
expr_stmt|;
name|assertIterSize
argument_list|(
literal|"required aaa: foo"
argument_list|,
literal|1
argument_list|,
name|aaa
operator|.
name|required
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a1"
block|,
literal|"a2"
block|}
argument_list|,
name|aaa
operator|.
name|getParams
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|aaa
operator|.
name|add
argument_list|(
literal|"yak"
argument_list|,
literal|"a3"
argument_list|)
expr_stmt|;
name|assertIterSize
argument_list|(
literal|"aaa: foo& yak"
argument_list|,
literal|2
argument_list|,
name|aaa
argument_list|)
expr_stmt|;
name|assertIterSize
argument_list|(
literal|"required aaa: foo& yak"
argument_list|,
literal|2
argument_list|,
name|aaa
operator|.
name|required
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a1"
block|,
literal|"a2"
block|}
argument_list|,
name|aaa
operator|.
name|getParams
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a3"
block|}
argument_list|,
name|aaa
operator|.
name|getParams
argument_list|(
literal|"yak"
argument_list|)
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|bbb
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|bbb
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"b1"
argument_list|)
expr_stmt|;
name|bbb
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"b2"
argument_list|)
expr_stmt|;
name|bbb
operator|.
name|add
argument_list|(
literal|"zot"
argument_list|,
literal|"b3"
argument_list|)
expr_stmt|;
name|assertIterSize
argument_list|(
literal|"bbb: foo& zot"
argument_list|,
literal|2
argument_list|,
name|bbb
argument_list|)
expr_stmt|;
name|assertIterSize
argument_list|(
literal|"required bbb: foo& zot"
argument_list|,
literal|2
argument_list|,
name|bbb
operator|.
name|required
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b1"
block|,
literal|"b2"
block|}
argument_list|,
name|bbb
operator|.
name|getParams
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b3"
block|}
argument_list|,
name|bbb
operator|.
name|getParams
argument_list|(
literal|"zot"
argument_list|)
argument_list|)
expr_stmt|;
name|SolrParams
name|def
init|=
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|aaa
argument_list|,
name|bbb
argument_list|)
decl_stmt|;
name|assertIterSize
argument_list|(
literal|"def: aaa + bbb"
argument_list|,
literal|3
argument_list|,
name|def
argument_list|)
expr_stmt|;
name|assertIterSize
argument_list|(
literal|"required def: aaa + bbb"
argument_list|,
literal|3
argument_list|,
name|def
operator|.
name|required
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a1"
block|,
literal|"a2"
block|}
argument_list|,
name|def
operator|.
name|getParams
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a3"
block|}
argument_list|,
name|def
operator|.
name|getParams
argument_list|(
literal|"yak"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b3"
block|}
argument_list|,
name|def
operator|.
name|getParams
argument_list|(
literal|"zot"
argument_list|)
argument_list|)
expr_stmt|;
name|SolrParams
name|append
init|=
name|SolrParams
operator|.
name|wrapAppended
argument_list|(
name|aaa
argument_list|,
name|bbb
argument_list|)
decl_stmt|;
name|assertIterSize
argument_list|(
literal|"append: aaa + bbb"
argument_list|,
literal|3
argument_list|,
name|append
argument_list|)
expr_stmt|;
name|assertIterSize
argument_list|(
literal|"required appended: aaa + bbb"
argument_list|,
literal|3
argument_list|,
name|append
operator|.
name|required
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a1"
block|,
literal|"a2"
block|,
literal|"b1"
block|,
literal|"b2"
block|, }
argument_list|,
name|append
operator|.
name|getParams
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a3"
block|}
argument_list|,
name|append
operator|.
name|getParams
argument_list|(
literal|"yak"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b3"
block|}
argument_list|,
name|append
operator|.
name|getParams
argument_list|(
literal|"zot"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiValues
specifier|public
name|void
name|testMultiValues
parameter_list|()
block|{
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"x"
argument_list|,
literal|"X1"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"x"
argument_list|,
literal|"X2"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"x"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"X3"
block|,
literal|"X4"
block|}
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|m
init|=
name|SolrParams
operator|.
name|toMultiMap
argument_list|(
name|nl
argument_list|)
decl_stmt|;
name|String
index|[]
name|r
init|=
name|m
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|r
argument_list|)
operator|.
name|containsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"X1"
block|,
literal|"X2"
block|,
literal|"X3"
block|,
literal|"X4"
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetAll
specifier|public
name|void
name|testGetAll
parameter_list|()
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"x"
argument_list|,
literal|"X1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"x"
argument_list|,
literal|"X2"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"y"
argument_list|,
literal|"Y"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
name|params
operator|.
name|getAll
argument_list|(
literal|null
argument_list|,
literal|"x"
argument_list|,
literal|"y"
argument_list|)
decl_stmt|;
name|String
index|[]
name|x
init|=
operator|(
name|String
index|[]
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|x
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"X1"
argument_list|,
name|x
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"X2"
argument_list|,
name|x
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Y"
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|params
operator|.
name|required
argument_list|()
operator|.
name|getAll
argument_list|(
literal|null
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Error expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|code
argument_list|()
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testModParamAddParams
specifier|public
name|void
name|testModParamAddParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|aaa
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|aaa
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"a1"
argument_list|)
expr_stmt|;
name|aaa
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"a2"
argument_list|)
expr_stmt|;
name|aaa
operator|.
name|add
argument_list|(
literal|"yak"
argument_list|,
literal|"a3"
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|bbb
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|bbb
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"b1"
argument_list|)
expr_stmt|;
name|bbb
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"b2"
argument_list|)
expr_stmt|;
name|bbb
operator|.
name|add
argument_list|(
literal|"zot"
argument_list|,
literal|"b3"
argument_list|)
expr_stmt|;
name|SolrParams
name|def
init|=
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|aaa
argument_list|,
name|bbb
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a1"
block|,
literal|"a2"
block|}
argument_list|,
name|def
operator|.
name|getParams
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a3"
block|}
argument_list|,
name|def
operator|.
name|getParams
argument_list|(
literal|"yak"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b3"
block|}
argument_list|,
name|def
operator|.
name|getParams
argument_list|(
literal|"zot"
argument_list|)
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|combined
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|combined
operator|.
name|add
argument_list|(
name|def
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a1"
block|,
literal|"a2"
block|}
argument_list|,
name|combined
operator|.
name|getParams
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a3"
block|}
argument_list|,
name|combined
operator|.
name|getParams
argument_list|(
literal|"yak"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b3"
block|}
argument_list|,
name|combined
operator|.
name|getParams
argument_list|(
literal|"zot"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetParams
specifier|public
name|void
name|testGetParams
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pmap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"str"
argument_list|,
literal|"string"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"bool"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"true-0"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"true-1"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"true-2"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"false-0"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"false-1"
argument_list|,
literal|"off"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"false-2"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"int"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"float"
argument_list|,
literal|"10.6"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.fl.str"
argument_list|,
literal|"string"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.fl.bool"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.fl.int"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.fl.float"
argument_list|,
literal|"10.6"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.bad.bool"
argument_list|,
literal|"notbool"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.bad.int"
argument_list|,
literal|"notint"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.bad.float"
argument_list|,
literal|"notfloat"
argument_list|)
expr_stmt|;
specifier|final
name|SolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
name|pmap
argument_list|)
decl_stmt|;
comment|// Test the string values we put in directly
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"10.6"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"float"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.fl.str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.fl.bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.fl.int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"10.6"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.fl.float"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"notbool"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.bad.bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"notint"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.bad.int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"notfloat"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.bad.float"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|pstr
init|=
literal|"string"
decl_stmt|;
specifier|final
name|Boolean
name|pbool
init|=
name|Boolean
operator|.
name|TRUE
decl_stmt|;
specifier|final
name|Integer
name|pint
init|=
operator|new
name|Integer
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|Float
name|pfloat
init|=
operator|new
name|Float
argument_list|(
literal|10.6f
argument_list|)
decl_stmt|;
comment|// Make sure they parse ok
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|params
operator|.
name|getInt
argument_list|(
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
argument_list|,
name|params
operator|.
name|getFloat
argument_list|(
literal|"float"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
literal|"f.fl.bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|params
operator|.
name|getInt
argument_list|(
literal|"f.fl.int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
argument_list|,
name|params
operator|.
name|getFloat
argument_list|(
literal|"f.fl.float"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|params
operator|.
name|getFieldParam
argument_list|(
literal|"fl"
argument_list|,
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|params
operator|.
name|getFieldBool
argument_list|(
literal|"fl"
argument_list|,
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|params
operator|.
name|getFieldInt
argument_list|(
literal|"fl"
argument_list|,
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
argument_list|,
name|params
operator|.
name|getFieldFloat
argument_list|(
literal|"fl"
argument_list|,
literal|"float"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test field defaulting (fall through to non-field-specific value)
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|params
operator|.
name|getFieldInt
argument_list|(
literal|"fff"
argument_list|,
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test boolean parsing
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
comment|// Must use Boolean rather than boolean reference value to prevent
comment|// auto-unboxing ambiguity
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
literal|"true-"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
literal|"false-"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Malformed params: These should throw a 400
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|params
operator|.
name|getInt
argument_list|(
literal|"f.bad.int"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|params
operator|.
name|getBool
argument_list|(
literal|"f.bad.bool"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|params
operator|.
name|getFloat
argument_list|(
literal|"f.bad.float"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ask for params that arent there
name|assertNull
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"asagdsaga"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|params
operator|.
name|getBool
argument_list|(
literal|"asagdsaga"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
literal|"asagdsaga"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|params
operator|.
name|getFloat
argument_list|(
literal|"asagdsaga"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get things with defaults
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"xxx"
argument_list|,
name|pstr
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
literal|"xxx"
argument_list|,
name|pbool
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
operator|.
name|intValue
argument_list|()
argument_list|,
name|params
operator|.
name|getInt
argument_list|(
literal|"xxx"
argument_list|,
name|pint
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
operator|.
name|floatValue
argument_list|()
argument_list|,
name|params
operator|.
name|getFloat
argument_list|(
literal|"xxx"
argument_list|,
name|pfloat
argument_list|)
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|params
operator|.
name|getFieldBool
argument_list|(
literal|"xxx"
argument_list|,
literal|"bool"
argument_list|,
name|pbool
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
operator|.
name|intValue
argument_list|()
argument_list|,
name|params
operator|.
name|getFieldInt
argument_list|(
literal|"xxx"
argument_list|,
literal|"int"
argument_list|,
name|pint
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
operator|.
name|floatValue
argument_list|()
argument_list|,
name|params
operator|.
name|getFieldFloat
argument_list|(
literal|"xxx"
argument_list|,
literal|"float"
argument_list|,
name|pfloat
argument_list|)
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|params
operator|.
name|getFieldParam
argument_list|(
literal|"xxx"
argument_list|,
literal|"str"
argument_list|,
name|pstr
argument_list|)
argument_list|)
expr_stmt|;
comment|// Required params testing uses decorator
specifier|final
name|SolrParams
name|required
init|=
name|params
operator|.
name|required
argument_list|()
decl_stmt|;
comment|// Required params which are present should test same as above
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|required
operator|.
name|get
argument_list|(
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|required
operator|.
name|getBool
argument_list|(
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|required
operator|.
name|getInt
argument_list|(
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
argument_list|,
name|required
operator|.
name|getFloat
argument_list|(
literal|"float"
argument_list|)
argument_list|)
expr_stmt|;
comment|// field value present
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|required
operator|.
name|getFieldBool
argument_list|(
literal|"fl"
argument_list|,
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
comment|// field defaulting (fall through to non-field-specific value)
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|required
operator|.
name|getFieldParams
argument_list|(
literal|"fakefield"
argument_list|,
literal|"str"
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|required
operator|.
name|getFieldParam
argument_list|(
literal|"fakefield"
argument_list|,
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|required
operator|.
name|getFieldBool
argument_list|(
literal|"fakefield"
argument_list|,
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|required
operator|.
name|getFieldInt
argument_list|(
literal|"fakefield"
argument_list|,
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
argument_list|,
name|required
operator|.
name|getFieldFloat
argument_list|(
literal|"fakefield"
argument_list|,
literal|"float"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Required params which are missing: These should throw a 400
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|required
operator|.
name|get
argument_list|(
literal|"aaaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|required
operator|.
name|getInt
argument_list|(
literal|"f.bad.int"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|required
operator|.
name|getBool
argument_list|(
literal|"f.bad.bool"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|required
operator|.
name|getFloat
argument_list|(
literal|"f.bad.float"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|required
operator|.
name|getInt
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|required
operator|.
name|getBool
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|required
operator|.
name|getFloat
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|params
operator|.
name|getFieldBool
argument_list|(
literal|"bad"
argument_list|,
literal|"bool"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
parameter_list|()
lambda|->
name|params
operator|.
name|getFieldInt
argument_list|(
literal|"bad"
argument_list|,
literal|"int"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fields with default use their parent value:
name|assertEquals
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"aaaa"
argument_list|,
literal|"str"
argument_list|)
argument_list|,
name|required
operator|.
name|get
argument_list|(
literal|"aaaa"
argument_list|,
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
literal|"f.bad.nnnn"
argument_list|,
name|pint
argument_list|)
argument_list|,
name|required
operator|.
name|getInt
argument_list|(
literal|"f.bad.nnnn"
argument_list|,
name|pint
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check default SolrParams
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|dmap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// these are not defined in params
name|dmap
operator|.
name|put
argument_list|(
literal|"dstr"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|dmap
operator|.
name|put
argument_list|(
literal|"dint"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
comment|// these are defined in params
name|dmap
operator|.
name|put
argument_list|(
literal|"int"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|SolrParams
name|defaults
init|=
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|params
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|dmap
argument_list|)
argument_list|)
decl_stmt|;
comment|// in params, not in default
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|defaults
operator|.
name|get
argument_list|(
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
comment|// in default, not in params
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|defaults
operator|.
name|get
argument_list|(
literal|"dstr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|123
argument_list|)
argument_list|,
name|defaults
operator|.
name|getInt
argument_list|(
literal|"dint"
argument_list|)
argument_list|)
expr_stmt|;
comment|// in params, overriding defaults
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|defaults
operator|.
name|getInt
argument_list|(
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
comment|// in neither params nor defaults
name|assertNull
argument_list|(
name|defaults
operator|.
name|get
argument_list|(
literal|"asagdsaga"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getReturnCode
specifier|public
specifier|static
name|int
name|getReturnCode
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
try|try
block|{
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|sx
parameter_list|)
block|{
return|return
name|sx
operator|.
name|code
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|500
return|;
block|}
return|return
literal|200
return|;
block|}
DECL|method|iterToList
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|iterToList
parameter_list|(
name|Iterator
argument_list|<
name|T
argument_list|>
name|iter
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|assertIterSize
specifier|static
name|void
name|assertIterSize
parameter_list|(
name|String
name|msg
parameter_list|,
name|int
name|expectedSize
parameter_list|,
name|SolrParams
name|p
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|iterToList
argument_list|(
name|p
operator|.
name|getParameterNamesIterator
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|msg
operator|+
literal|" "
operator|+
name|keys
operator|.
name|toString
argument_list|()
argument_list|,
name|expectedSize
argument_list|,
name|keys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

