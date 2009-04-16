begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|Collections
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

begin_comment
comment|/**  *<p>  * Test for FileListEntityProcessor  *</p>  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestFileListEntityProcessor
specifier|public
class|class
name|TestFileListEntityProcessor
block|{
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|File
name|tmpdir
init|=
operator|new
name|File
argument_list|(
literal|"."
operator|+
name|time
argument_list|)
decl_stmt|;
name|tmpdir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|tmpdir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"a.xml"
argument_list|,
literal|"a.xml"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"b.xml"
argument_list|,
literal|"b.xml"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"c.props"
argument_list|,
literal|"c.props"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Map
name|attrs
init|=
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|"xml$"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|Context
name|c
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolverImpl
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|FileListEntityProcessor
name|fileListEntityProcessor
init|=
operator|new
name|FileListEntityProcessor
argument_list|()
decl_stmt|;
name|fileListEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|f
init|=
name|fileListEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
break|break;
name|fList
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
name|FileListEntityProcessor
operator|.
name|ABSOLUTE_FILE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNTOT
specifier|public
name|void
name|testNTOT
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|File
name|tmpdir
init|=
operator|new
name|File
argument_list|(
literal|"."
operator|+
name|time
argument_list|)
decl_stmt|;
name|tmpdir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|tmpdir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"a.xml"
argument_list|,
literal|"a.xml"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"b.xml"
argument_list|,
literal|"b.xml"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|tmpdir
argument_list|,
literal|"c.props"
argument_list|,
literal|"c.props"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Map
name|attrs
init|=
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|"xml$"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|FileListEntityProcessor
operator|.
name|OLDER_THAN
argument_list|,
literal|"'NOW'"
argument_list|)
decl_stmt|;
name|Context
name|c
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolverImpl
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|FileListEntityProcessor
name|fileListEntityProcessor
init|=
operator|new
name|FileListEntityProcessor
argument_list|()
decl_stmt|;
name|fileListEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|f
init|=
name|fileListEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
break|break;
name|fList
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
name|FileListEntityProcessor
operator|.
name|ABSOLUTE_FILE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"List of files when given OLDER_THAN -- "
operator|+
name|fList
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|attrs
operator|=
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|".xml$"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|FileListEntityProcessor
operator|.
name|NEWER_THAN
argument_list|,
literal|"'NOW-2HOURS'"
argument_list|)
expr_stmt|;
name|c
operator|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolverImpl
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|fileListEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|fList
operator|.
name|clear
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|f
init|=
name|fileListEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
break|break;
name|fList
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
name|FileListEntityProcessor
operator|.
name|ABSOLUTE_FILE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"List of files when given NEWER_THAN -- "
operator|+
name|fList
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRECURSION
specifier|public
name|void
name|testRECURSION
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|File
name|tmpdir
init|=
operator|new
name|File
argument_list|(
literal|"."
operator|+
name|time
argument_list|)
decl_stmt|;
name|tmpdir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|tmpdir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|File
name|childdir
init|=
operator|new
name|File
argument_list|(
name|tmpdir
operator|+
literal|"/child"
argument_list|)
decl_stmt|;
name|childdir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|childdir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|createFile
argument_list|(
name|childdir
argument_list|,
literal|"a.xml"
argument_list|,
literal|"a.xml"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|childdir
argument_list|,
literal|"b.xml"
argument_list|,
literal|"b.xml"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createFile
argument_list|(
name|childdir
argument_list|,
literal|"c.props"
argument_list|,
literal|"c.props"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Map
name|attrs
init|=
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
name|FileListEntityProcessor
operator|.
name|FILE_NAME
argument_list|,
literal|"^.*\\.xml$"
argument_list|,
name|FileListEntityProcessor
operator|.
name|BASE_DIR
argument_list|,
name|childdir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|FileListEntityProcessor
operator|.
name|RECURSIVE
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|Context
name|c
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
operator|new
name|VariableResolverImpl
argument_list|()
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|FileListEntityProcessor
name|fileListEntityProcessor
init|=
operator|new
name|FileListEntityProcessor
argument_list|()
decl_stmt|;
name|fileListEntityProcessor
operator|.
name|init
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// Add the documents to the index. NextRow() should only
comment|// find two filesnames that match the pattern in fileName
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|f
init|=
name|fileListEntityProcessor
operator|.
name|nextRow
argument_list|()
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
break|break;
name|fList
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|f
operator|.
name|get
argument_list|(
name|FileListEntityProcessor
operator|.
name|ABSOLUTE_FILE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"List of files indexed -- "
operator|+
name|fList
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createFile
specifier|public
specifier|static
name|File
name|createFile
parameter_list|(
name|File
name|tmpdir
parameter_list|,
name|String
name|name
parameter_list|,
name|byte
index|[]
name|content
parameter_list|,
name|boolean
name|changeModifiedTime
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|tmpdir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|name
argument_list|)
decl_stmt|;
name|file
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|FileOutputStream
name|f
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|f
operator|.
name|write
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|f
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// System.out.println("before "+file.lastModified());
if|if
condition|(
name|changeModifiedTime
condition|)
name|file
operator|.
name|setLastModified
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|3600000
argument_list|)
expr_stmt|;
comment|// System.out.println("after "+file.lastModified());
return|return
name|file
return|;
block|}
block|}
end_class

end_unit

