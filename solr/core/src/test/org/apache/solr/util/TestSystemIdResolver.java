begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|analysis
operator|.
name|util
operator|.
name|ResourceLoader
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
name|core
operator|.
name|SolrResourceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_class
DECL|class|TestSystemIdResolver
specifier|public
class|class
name|TestSystemIdResolver
extends|extends
name|LuceneTestCase
block|{
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.allow.unsafe.resourceloading"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.allow.unsafe.resourceloading"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|assertEntityResolving
specifier|private
name|void
name|assertEntityResolving
parameter_list|(
name|SystemIdResolver
name|resolver
parameter_list|,
name|String
name|expectedSystemId
parameter_list|,
name|String
name|base
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|InputSource
name|is
init|=
name|resolver
operator|.
name|resolveEntity
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|base
argument_list|,
name|systemId
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"Resolved SystemId does not match"
argument_list|,
name|expectedSystemId
argument_list|,
name|is
operator|.
name|getSystemId
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
operator|.
name|getByteStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testResolving
specifier|public
name|void
name|testResolving
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|testHome
init|=
name|SolrTestCaseJ4
operator|.
name|getFile
argument_list|(
literal|"solr/collection1"
argument_list|)
operator|.
name|getParentFile
argument_list|()
operator|.
name|toPath
argument_list|()
decl_stmt|;
specifier|final
name|ResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|testHome
operator|.
name|resolve
argument_list|(
literal|"collection1"
argument_list|)
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SystemIdResolver
name|resolver
init|=
operator|new
name|SystemIdResolver
argument_list|(
name|loader
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fileUri
init|=
operator|new
name|File
argument_list|(
name|testHome
operator|+
literal|"/crazy-path-to-config.xml"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"solrres:/test.xml"
argument_list|,
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
literal|"test.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"solrres://@/usr/local/etc/test.xml"
argument_list|,
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
literal|"/usr/local/etc/test.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"solrres://@/test.xml"
argument_list|,
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
name|File
operator|.
name|separatorChar
operator|+
literal|"test.xml"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check relative URI resolving
name|assertEquals
argument_list|(
literal|"solrres:/test.xml"
argument_list|,
name|resolver
operator|.
name|resolveRelativeURI
argument_list|(
literal|"solrres:/base.xml"
argument_list|,
literal|"test.xml"
argument_list|)
operator|.
name|toASCIIString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"solrres://@/etc/test.xml"
argument_list|,
name|resolver
operator|.
name|resolveRelativeURI
argument_list|(
literal|"solrres://@/usr/local/etc/base.xml"
argument_list|,
literal|"../../../etc/test.xml"
argument_list|)
operator|.
name|toASCIIString
argument_list|()
argument_list|)
expr_stmt|;
comment|// special case: if relative URI starts with "/" convert to an absolute solrres://@/-URI
name|assertEquals
argument_list|(
literal|"solrres://@/a/test.xml"
argument_list|,
name|resolver
operator|.
name|resolveRelativeURI
argument_list|(
literal|"solrres:/base.xml"
argument_list|,
literal|"/a/test.xml"
argument_list|)
operator|.
name|toASCIIString
argument_list|()
argument_list|)
expr_stmt|;
comment|// test, that resolving works if somebody uses an absolute file:-URI in a href attribute, it should be preserved
name|assertEquals
argument_list|(
name|fileUri
argument_list|,
name|resolver
operator|.
name|resolveRelativeURI
argument_list|(
literal|"solrres:/base.xml"
argument_list|,
name|fileUri
argument_list|)
operator|.
name|toASCIIString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"solrres:/base.xml"
argument_list|,
name|resolver
operator|.
name|resolveRelativeURI
argument_list|(
name|fileUri
argument_list|,
literal|"solrres:/base.xml"
argument_list|)
operator|.
name|toASCIIString
argument_list|()
argument_list|)
expr_stmt|;
comment|// do some real resolves to InputStreams with real existing files
name|assertEntityResolving
argument_list|(
name|resolver
argument_list|,
literal|"solrres:/schema.xml"
argument_list|,
literal|"solrres:/solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|assertEntityResolving
argument_list|(
name|resolver
argument_list|,
literal|"solrres:/org/apache/solr/util/TestSystemIdResolver.class"
argument_list|,
literal|"solrres:/org/apache/solr/util/RTimer.class"
argument_list|,
literal|"TestSystemIdResolver.class"
argument_list|)
expr_stmt|;
name|assertEntityResolving
argument_list|(
name|resolver
argument_list|,
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
name|testHome
operator|+
literal|"/collection1/conf/schema.xml"
argument_list|)
argument_list|,
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
name|testHome
operator|+
literal|"/collection1/conf/solrconfig.xml"
argument_list|)
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|assertEntityResolving
argument_list|(
name|resolver
argument_list|,
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
name|testHome
operator|+
literal|"/crazy-path-to-schema.xml"
argument_list|)
argument_list|,
name|SystemIdResolver
operator|.
name|createSystemIdFromResourceName
argument_list|(
name|testHome
operator|+
literal|"/crazy-path-to-config.xml"
argument_list|)
argument_list|,
literal|"crazy-path-to-schema.xml"
argument_list|)
expr_stmt|;
comment|// test, that resolving works if somebody uses an absolute file:-URI in a href attribute, the resolver should return null (default fallback)
name|assertNull
argument_list|(
name|resolver
operator|.
name|resolveEntity
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"solrres:/solrconfig.xml"
argument_list|,
name|fileUri
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

