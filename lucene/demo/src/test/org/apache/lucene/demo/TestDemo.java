begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
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
name|ByteArrayOutputStream
import|;
end_import

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
name|PrintStream
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
name|Charset
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
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestDemo
specifier|public
class|class
name|TestDemo
extends|extends
name|LuceneTestCase
block|{
DECL|method|testOneSearch
specifier|private
name|void
name|testOneSearch
parameter_list|(
name|File
name|indexPath
parameter_list|,
name|String
name|query
parameter_list|,
name|int
name|expectedHitCount
parameter_list|)
throws|throws
name|Exception
block|{
name|PrintStream
name|outSave
init|=
name|System
operator|.
name|out
decl_stmt|;
try|try
block|{
name|ByteArrayOutputStream
name|bytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|fakeSystemOut
init|=
operator|new
name|PrintStream
argument_list|(
name|bytes
argument_list|,
literal|false
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|fakeSystemOut
argument_list|)
expr_stmt|;
name|SearchFiles
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-query"
block|,
name|query
block|,
literal|"-index"
block|,
name|indexPath
operator|.
name|getPath
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|fakeSystemOut
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|output
init|=
name|bytes
operator|.
name|toString
argument_list|(
name|Charset
operator|.
name|defaultCharset
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
comment|// intentionally use default encoding
name|assertTrue
argument_list|(
literal|"output="
operator|+
name|output
argument_list|,
name|output
operator|.
name|contains
argument_list|(
name|expectedHitCount
operator|+
literal|" total matching documents"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|outSave
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIndexSearch
specifier|public
name|void
name|testIndexSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dir
init|=
name|getDataFile
argument_list|(
literal|"test-files/docs"
argument_list|)
decl_stmt|;
name|File
name|indexDir
init|=
name|TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"ContribDemoTest"
argument_list|)
decl_stmt|;
name|IndexFiles
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-create"
block|,
literal|"-docs"
block|,
name|dir
operator|.
name|getPath
argument_list|()
block|,
literal|"-index"
block|,
name|indexDir
operator|.
name|getPath
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|testOneSearch
argument_list|(
name|indexDir
argument_list|,
literal|"apache"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|testOneSearch
argument_list|(
name|indexDir
argument_list|,
literal|"patent"
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|testOneSearch
argument_list|(
name|indexDir
argument_list|,
literal|"lucene"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testOneSearch
argument_list|(
name|indexDir
argument_list|,
literal|"gnu"
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|testOneSearch
argument_list|(
name|indexDir
argument_list|,
literal|"derivative"
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|testOneSearch
argument_list|(
name|indexDir
argument_list|,
literal|"license"
argument_list|,
literal|13
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

