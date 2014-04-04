begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package

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
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|core
operator|.
name|SolrResourceLoader
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ContentStreamTest
specifier|public
class|class
name|ContentStreamTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testStringStream
specifier|public
name|void
name|testStringStream
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|input
init|=
literal|"aads ghaskdgasgldj asl sadg ajdsg&jag # @ hjsakg hsakdg hjkas s"
decl_stmt|;
name|ContentStreamBase
name|stream
init|=
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|,
name|stream
operator|.
name|getSize
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|input
argument_list|,
name|IOUtils
operator|.
name|toString
argument_list|(
name|stream
operator|.
name|getStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|input
argument_list|,
name|IOUtils
operator|.
name|toString
argument_list|(
name|stream
operator|.
name|getReader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFileStream
specifier|public
name|void
name|testFileStream
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|openResource
argument_list|(
literal|"solrj/README"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|File
name|file
init|=
name|TestUtil
operator|.
name|createTempFile
argument_list|(
literal|"README"
argument_list|,
literal|""
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|ContentStreamBase
name|stream
init|=
operator|new
name|ContentStreamBase
operator|.
name|FileStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|InputStream
name|s
init|=
name|stream
operator|.
name|getStream
argument_list|()
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|InputStreamReader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|Reader
name|r
init|=
name|stream
operator|.
name|getReader
argument_list|()
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|,
name|stream
operator|.
name|getSize
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
name|fis
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
name|isr
argument_list|,
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|isr
operator|.
name|close
argument_list|()
expr_stmt|;
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testURLStream
specifier|public
name|void
name|testURLStream
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|openResource
argument_list|(
literal|"solrj/README"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|createTempDir
argument_list|()
argument_list|,
literal|"README"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|ContentStreamBase
name|stream
init|=
operator|new
name|ContentStreamBase
operator|.
name|URLStream
argument_list|(
operator|new
name|URL
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|InputStream
name|s
init|=
name|stream
operator|.
name|getStream
argument_list|()
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|FileInputStream
name|fis2
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|InputStreamReader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|Reader
name|r
init|=
name|stream
operator|.
name|getReader
argument_list|()
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
name|fis2
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|,
name|stream
operator|.
name|getSize
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|IOUtils
operator|.
name|contentEquals
argument_list|(
name|isr
argument_list|,
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|file
operator|.
name|length
argument_list|()
argument_list|,
name|stream
operator|.
name|getSize
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
name|isr
operator|.
name|close
argument_list|()
expr_stmt|;
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
name|fis2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

