begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
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
name|params
operator|.
name|CommonParams
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
name|params
operator|.
name|MapSolrParams
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * This is a simple test to make sure the<code>CopyField</code> works.  * It uses its own special schema file.  *  * @since solr 1.4  */
end_comment

begin_class
DECL|class|CopyFieldTest
specifier|public
class|class
name|CopyFieldTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema-copyfield-test.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyFieldSchemaFieldSchemaField
specifier|public
name|void
name|testCopyFieldSchemaFieldSchemaField
parameter_list|()
block|{
try|try
block|{
operator|new
name|CopyField
argument_list|(
operator|new
name|SchemaField
argument_list|(
literal|"source"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"CopyField failed with null SchemaField argument."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"can't be NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|CopyField
argument_list|(
literal|null
argument_list|,
operator|new
name|SchemaField
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"CopyField failed with null SchemaField argument."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"can't be NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|CopyField
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"CopyField failed with null SchemaField argument."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"can't be NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCopyFieldSchemaFieldSchemaFieldInt
specifier|public
name|void
name|testCopyFieldSchemaFieldSchemaFieldInt
parameter_list|()
block|{
try|try
block|{
operator|new
name|CopyField
argument_list|(
literal|null
argument_list|,
operator|new
name|SchemaField
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"CopyField failed with null SchemaField argument."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"can't be NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|CopyField
argument_list|(
operator|new
name|SchemaField
argument_list|(
literal|"source"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"CopyField failed with null SchemaField argument."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"can't be NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|CopyField
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"CopyField failed with null SchemaField argument."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"can't be NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|CopyField
argument_list|(
operator|new
name|SchemaField
argument_list|(
literal|"source"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
operator|new
name|SchemaField
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
operator|-
literal|1000
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"CopyField failed with negative length argument."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getLocalizedMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"can't have a negative value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
operator|new
name|CopyField
argument_list|(
operator|new
name|SchemaField
argument_list|(
literal|"source"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
operator|new
name|SchemaField
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
name|CopyField
operator|.
name|UNLIMITED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSource
specifier|public
name|void
name|testGetSource
parameter_list|()
block|{
specifier|final
name|CopyField
name|copyField
init|=
operator|new
name|CopyField
argument_list|(
operator|new
name|SchemaField
argument_list|(
literal|"source"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
operator|new
name|SchemaField
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"source"
argument_list|,
name|copyField
operator|.
name|getSource
argument_list|()
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetDestination
specifier|public
name|void
name|testGetDestination
parameter_list|()
block|{
specifier|final
name|CopyField
name|copyField
init|=
operator|new
name|CopyField
argument_list|(
operator|new
name|SchemaField
argument_list|(
literal|"source"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
operator|new
name|SchemaField
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"destination"
argument_list|,
name|copyField
operator|.
name|getDestination
argument_list|()
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetMaxChars
specifier|public
name|void
name|testGetMaxChars
parameter_list|()
block|{
specifier|final
name|CopyField
name|copyField
init|=
operator|new
name|CopyField
argument_list|(
operator|new
name|SchemaField
argument_list|(
literal|"source"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
operator|new
name|SchemaField
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|TextField
argument_list|()
argument_list|)
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|copyField
operator|.
name|getMaxChars
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyFieldFunctionality
specifier|public
name|void
name|testCopyFieldFunctionality
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"title"
argument_list|,
literal|"test copy field"
argument_list|,
literal|"text_en"
argument_list|,
literal|"this is a simple test of the copy field functionality"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"text_en:simple"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Make sure they got in"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='10']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"highlight:simple"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"dynamic source"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='10']"
argument_list|,
literal|"//result/doc[1]/arr[@name='highlight']/str[.='this is a simple test of ']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"text_en:functionality"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Make sure they got in"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"highlight:functionality"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"dynamic source"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExplicitSourceGlob
specifier|public
name|void
name|testExplicitSourceGlob
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"schema should contain explicit field 'sku1'"
argument_list|,
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"sku1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"schema should contain explicit field 'sku2'"
argument_list|,
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"sku2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"'sku*' should not be (or match) a dynamic field"
argument_list|,
name|schema
operator|.
name|getDynamicPattern
argument_list|(
literal|"sku*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"schema should contain dynamic field '*_s'"
argument_list|,
name|schema
operator|.
name|getDynamicPattern
argument_list|(
literal|"*_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"*_s"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|subsetPattern
init|=
literal|"*_dest_sub_s"
decl_stmt|;
specifier|final
name|String
name|dynamicPattern1
init|=
name|schema
operator|.
name|getDynamicPattern
argument_list|(
name|subsetPattern
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"'"
operator|+
name|subsetPattern
operator|+
literal|"' should match dynamic field '*_s', but instead matches '"
operator|+
name|dynamicPattern1
operator|+
literal|"'"
argument_list|,
name|dynamicPattern1
operator|.
name|equals
argument_list|(
literal|"*_s"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|dest_sub_no_ast_s
init|=
literal|"dest_sub_no_ast_s"
decl_stmt|;
name|assertFalse
argument_list|(
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|dest_sub_no_ast_s
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should not be an explicit field
specifier|final
name|String
name|dynamicPattern2
init|=
name|schema
operator|.
name|getDynamicPattern
argument_list|(
name|dest_sub_no_ast_s
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"'"
operator|+
name|dest_sub_no_ast_s
operator|+
literal|"' should match dynamic field '*_s', but instead matches '"
operator|+
name|dynamicPattern2
operator|+
literal|"'"
argument_list|,
name|dynamicPattern2
operator|.
name|equals
argument_list|(
literal|"*_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"sku1"
argument_list|,
literal|"10-1839ACX-93"
argument_list|,
literal|"sku2"
argument_list|,
literal|"AAM46"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"text:AAM46"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"sku2 copied to text"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='5']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"1_s:10-1839ACX-93"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"sku1 copied to dynamic dest *_s"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='5']"
argument_list|,
literal|"//result/doc[1]/arr[@name='sku1']/str[.='10-1839ACX-93']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"1_dest_sub_s:10-1839ACX-93"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"sku1 copied to *_dest_sub_s (*_s subset pattern)"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"dest_sub_no_ast_s:AAM46"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"sku2 copied to dest_sub_no_ast_s (*_s subset pattern no asterisk)"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSourceGlobMatchesNoDynamicOrExplicitField
specifier|public
name|void
name|testSourceGlobMatchesNoDynamicOrExplicitField
parameter_list|()
block|{
comment|// SOLR-4650: copyField source globs should not have to match an explicit or dynamic field
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"'testing123_*' should not be (or match) a dynamic or explicit field"
argument_list|,
name|schema
operator|.
name|getFieldOrNull
argument_list|(
literal|"testing123_*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"schema should contain dynamic field '*_s'"
argument_list|,
name|schema
operator|.
name|getDynamicPattern
argument_list|(
literal|"*_s"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"*_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"sku1"
argument_list|,
literal|"10-1839ACX-93"
argument_list|,
literal|"testing123_s"
argument_list|,
literal|"AAM46"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"text:AAM46"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"sku2 copied to text"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='5']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCatchAllCopyField
specifier|public
name|void
name|testCatchAllCopyField
parameter_list|()
block|{
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"'*' should not be (or match) a dynamic field"
argument_list|,
name|schema
operator|.
name|getDynamicPattern
argument_list|(
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"sku1"
argument_list|,
literal|"10-1839ACX-93"
argument_list|,
literal|"testing123_s"
argument_list|,
literal|"AAM46"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|q
range|:
operator|new
name|String
index|[]
block|{
literal|"5"
block|,
literal|"10-1839ACX-93"
block|,
literal|"AAM46"
block|}
control|)
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"catchall_t:"
operator|+
name|q
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='5']"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

