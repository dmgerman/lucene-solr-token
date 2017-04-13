begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|lucene50
operator|.
name|Lucene50StoredFieldsFormat
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
name|codecs
operator|.
name|lucene50
operator|.
name|Lucene50StoredFieldsFormat
operator|.
name|Mode
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
name|codecs
operator|.
name|perfield
operator|.
name|PerFieldDocValuesFormat
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
name|codecs
operator|.
name|perfield
operator|.
name|PerFieldPostingsFormat
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
name|index
operator|.
name|SegmentInfo
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
name|index
operator|.
name|SegmentInfos
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
name|TestUtil
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|IndexSchemaFactory
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
name|schema
operator|.
name|SchemaField
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
name|search
operator|.
name|SolrIndexSearcher
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
name|util
operator|.
name|RefCounted
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
name|util
operator|.
name|TestHarness
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

begin_class
DECL|class|TestCodecSupport
specifier|public
class|class
name|TestCodecSupport
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
literal|"solrconfig_codec.xml"
argument_list|,
literal|"schema_codec.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPostingsFormats
specifier|public
name|void
name|testPostingsFormats
parameter_list|()
block|{
name|Codec
name|codec
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getCodec
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SchemaField
argument_list|>
name|fields
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|SchemaField
name|schemaField
init|=
name|fields
operator|.
name|get
argument_list|(
literal|"string_direct_f"
argument_list|)
decl_stmt|;
name|PerFieldPostingsFormat
name|format
init|=
operator|(
name|PerFieldPostingsFormat
operator|)
name|codec
operator|.
name|postingsFormat
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Direct"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|schemaField
operator|=
name|fields
operator|.
name|get
argument_list|(
literal|"string_standard_f"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestUtil
operator|.
name|getDefaultPostingsFormat
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|schemaField
operator|=
name|fields
operator|.
name|get
argument_list|(
literal|"string_f"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestUtil
operator|.
name|getDefaultPostingsFormat
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocValuesFormats
specifier|public
name|void
name|testDocValuesFormats
parameter_list|()
block|{
name|Codec
name|codec
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getCodec
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SchemaField
argument_list|>
name|fields
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|SchemaField
name|schemaField
init|=
name|fields
operator|.
name|get
argument_list|(
literal|"string_disk_f"
argument_list|)
decl_stmt|;
name|PerFieldDocValuesFormat
name|format
init|=
operator|(
name|PerFieldDocValuesFormat
operator|)
name|codec
operator|.
name|docValuesFormat
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|TestUtil
operator|.
name|getDefaultDocValuesFormat
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|format
operator|.
name|getDocValuesFormatForField
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|schemaField
operator|=
name|fields
operator|.
name|get
argument_list|(
literal|"string_memory_f"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Memory"
argument_list|,
name|format
operator|.
name|getDocValuesFormatForField
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|schemaField
operator|=
name|fields
operator|.
name|get
argument_list|(
literal|"string_f"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestUtil
operator|.
name|getDefaultDocValuesFormat
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|format
operator|.
name|getDocValuesFormatForField
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDynamicFieldsPostingsFormats
specifier|public
name|void
name|testDynamicFieldsPostingsFormats
parameter_list|()
block|{
name|Codec
name|codec
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getCodec
argument_list|()
decl_stmt|;
name|PerFieldPostingsFormat
name|format
init|=
operator|(
name|PerFieldPostingsFormat
operator|)
name|codec
operator|.
name|postingsFormat
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Direct"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"foo_direct"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Direct"
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"bar_direct"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestUtil
operator|.
name|getDefaultPostingsFormat
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"foo_standard"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestUtil
operator|.
name|getDefaultPostingsFormat
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|format
operator|.
name|getPostingsFormatForField
argument_list|(
literal|"bar_standard"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDynamicFieldsDocValuesFormats
specifier|public
name|void
name|testDynamicFieldsDocValuesFormats
parameter_list|()
block|{
name|Codec
name|codec
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getCodec
argument_list|()
decl_stmt|;
name|PerFieldDocValuesFormat
name|format
init|=
operator|(
name|PerFieldDocValuesFormat
operator|)
name|codec
operator|.
name|docValuesFormat
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|TestUtil
operator|.
name|getDefaultDocValuesFormat
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|format
operator|.
name|getDocValuesFormatForField
argument_list|(
literal|"foo_disk"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestUtil
operator|.
name|getDefaultDocValuesFormat
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|format
operator|.
name|getDocValuesFormatForField
argument_list|(
literal|"bar_disk"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Memory"
argument_list|,
name|format
operator|.
name|getDocValuesFormatForField
argument_list|(
literal|"foo_memory"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Memory"
argument_list|,
name|format
operator|.
name|getDocValuesFormatForField
argument_list|(
literal|"bar_memory"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|reloadCoreAndRecreateIndex
specifier|private
name|void
name|reloadCoreAndRecreateIndex
parameter_list|()
block|{
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|h
operator|.
name|coreName
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"string_f"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestCompressionMode
specifier|private
name|void
name|doTestCompressionMode
parameter_list|(
name|String
name|propertyValue
parameter_list|,
name|String
name|expectedModeString
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|propertyValue
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.COMPRESSION_MODE"
argument_list|,
name|propertyValue
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|reloadCoreAndRecreateIndex
argument_list|()
expr_stmt|;
name|assertCompressionMode
argument_list|(
name|expectedModeString
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tests.COMPRESSION_MODE"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertCompressionMode
specifier|protected
name|void
name|assertCompressionMode
parameter_list|(
name|String
name|expectedModeString
parameter_list|,
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|ref
init|=
literal|null
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ref
operator|=
name|core
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|ref
operator|.
name|get
argument_list|()
expr_stmt|;
name|SegmentInfos
name|infos
init|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|directory
argument_list|()
argument_list|)
decl_stmt|;
name|SegmentInfo
name|info
init|=
name|infos
operator|.
name|info
argument_list|(
name|infos
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|info
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expecting compression mode string to be "
operator|+
name|expectedModeString
operator|+
literal|" but got: "
operator|+
name|info
operator|.
name|getAttribute
argument_list|(
name|Lucene50StoredFieldsFormat
operator|.
name|MODE_KEY
argument_list|)
operator|+
literal|"\n SegmentInfo: "
operator|+
name|info
operator|+
literal|"\n SegmentInfos: "
operator|+
name|infos
operator|+
literal|"\n Codec: "
operator|+
name|core
operator|.
name|getCodec
argument_list|()
argument_list|,
name|expectedModeString
argument_list|,
name|info
operator|.
name|getAttribute
argument_list|(
name|Lucene50StoredFieldsFormat
operator|.
name|MODE_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
name|ref
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testCompressionMode
specifier|public
name|void
name|testCompressionMode
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"incompatible change in compressionMode property"
argument_list|,
literal|"compressionMode"
argument_list|,
name|SchemaCodecFactory
operator|.
name|COMPRESSION_MODE
argument_list|)
expr_stmt|;
name|doTestCompressionMode
argument_list|(
literal|"BEST_SPEED"
argument_list|,
literal|"BEST_SPEED"
argument_list|)
expr_stmt|;
name|doTestCompressionMode
argument_list|(
literal|"BEST_COMPRESSION"
argument_list|,
literal|"BEST_COMPRESSION"
argument_list|)
expr_stmt|;
name|doTestCompressionMode
argument_list|(
literal|"best_speed"
argument_list|,
literal|"BEST_SPEED"
argument_list|)
expr_stmt|;
name|doTestCompressionMode
argument_list|(
literal|"best_compression"
argument_list|,
literal|"BEST_COMPRESSION"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMixedCompressionMode
specifier|public
name|void
name|testMixedCompressionMode
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.COMPRESSION_MODE"
argument_list|,
literal|"BEST_SPEED"
argument_list|)
expr_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|h
operator|.
name|coreName
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"string_f"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"foo bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertCompressionMode
argument_list|(
literal|"BEST_SPEED"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.COMPRESSION_MODE"
argument_list|,
literal|"BEST_COMPRESSION"
argument_list|)
expr_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|h
operator|.
name|coreName
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"string_f"
argument_list|,
literal|"2"
argument_list|,
literal|"text"
argument_list|,
literal|"foo zar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertCompressionMode
argument_list|(
literal|"BEST_COMPRESSION"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.COMPRESSION_MODE"
argument_list|,
literal|"BEST_SPEED"
argument_list|)
expr_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|h
operator|.
name|coreName
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"string_f"
argument_list|,
literal|"3"
argument_list|,
literal|"text"
argument_list|,
literal|"foo zoo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertCompressionMode
argument_list|(
literal|"BEST_SPEED"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:foo"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertCompressionMode
argument_list|(
literal|"BEST_SPEED"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tests.COMPRESSION_MODE"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBadCompressionMode
specifier|public
name|void
name|testBadCompressionMode
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|doTestCompressionMode
argument_list|(
literal|"something_that_doesnt_exist"
argument_list|,
literal|"something_that_doesnt_exist"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expecting exception"
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
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected Exception message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unable to reload core"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SchemaCodecFactory
name|factory
init|=
operator|new
name|SchemaCodecFactory
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|SchemaCodecFactory
operator|.
name|COMPRESSION_MODE
argument_list|,
literal|"something_that_doesnt_exist"
argument_list|)
expr_stmt|;
try|try
block|{
name|factory
operator|.
name|init
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expecting exception"
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
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected Exception message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid compressionMode: 'something_that_doesnt_exist'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|factory
operator|=
operator|new
name|SchemaCodecFactory
argument_list|()
expr_stmt|;
name|nl
operator|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|SchemaCodecFactory
operator|.
name|COMPRESSION_MODE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
try|try
block|{
name|factory
operator|.
name|init
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expecting exception"
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
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
operator|.
name|code
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected Exception message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid compressionMode: ''"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCompressionModeDefault
specifier|public
name|void
name|testCompressionModeDefault
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|"Default Solr compression mode changed. Is this expected?"
argument_list|,
name|SchemaCodecFactory
operator|.
name|SOLR_DEFAULT_COMPRESSION_MODE
argument_list|,
name|Mode
operator|.
name|valueOf
argument_list|(
literal|"BEST_SPEED"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|previousCoreName
init|=
name|h
operator|.
name|coreName
decl_stmt|;
name|String
name|newCoreName
init|=
literal|"core_with_default_compression"
decl_stmt|;
name|SolrCore
name|c
init|=
literal|null
decl_stmt|;
name|SolrConfig
name|config
init|=
name|TestHarness
operator|.
name|createConfig
argument_list|(
name|testSolrHome
argument_list|,
name|previousCoreName
argument_list|,
literal|"solrconfig_codec2.xml"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected codec factory for this test."
argument_list|,
literal|"solr.SchemaCodecFactory"
argument_list|,
name|config
operator|.
name|get
argument_list|(
literal|"codecFactory/@class"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Unexpected configuration of codec factory for this test. Expecting empty element"
argument_list|,
name|config
operator|.
name|getNode
argument_list|(
literal|"codecFactory"
argument_list|,
literal|false
argument_list|)
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSchema
name|schema
init|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
literal|"schema_codec.xml"
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|CoreContainer
name|coreContainer
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
try|try
block|{
name|CoreDescriptor
name|cd
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|newCoreName
argument_list|,
name|testSolrHome
operator|.
name|resolve
argument_list|(
name|newCoreName
argument_list|)
argument_list|,
name|coreContainer
operator|.
name|getContainerProperties
argument_list|()
argument_list|,
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
argument_list|)
decl_stmt|;
name|c
operator|=
operator|new
name|SolrCore
argument_list|(
name|coreContainer
argument_list|,
name|cd
argument_list|,
operator|new
name|ConfigSet
argument_list|(
literal|"fakeConfigset"
argument_list|,
name|config
argument_list|,
name|schema
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|coreContainer
operator|.
name|registerCore
argument_list|(
name|cd
argument_list|,
name|c
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|h
operator|.
name|coreName
operator|=
name|newCoreName
expr_stmt|;
name|assertEquals
argument_list|(
literal|"We are not using the correct core"
argument_list|,
literal|"solrconfig_codec2.xml"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getConfigResource
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"string_f"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertCompressionMode
argument_list|(
name|SchemaCodecFactory
operator|.
name|SOLR_DEFAULT_COMPRESSION_MODE
operator|.
name|name
argument_list|()
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|h
operator|.
name|coreName
operator|=
name|previousCoreName
expr_stmt|;
name|coreContainer
operator|.
name|unload
argument_list|(
name|newCoreName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

