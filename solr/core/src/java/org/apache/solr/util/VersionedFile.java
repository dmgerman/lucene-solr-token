begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilenameFilter
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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|Arrays
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
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  *   * @since solr 1.3  */
end_comment

begin_class
DECL|class|VersionedFile
specifier|public
class|class
name|VersionedFile
block|{
comment|/* Open the latest version of a file... fileName if that exists, or    * the last fileName.* after being sorted lexicographically.    * Older versions of the file are deleted (and queued for deletion if    * that fails).    */
DECL|method|getLatestFile
specifier|public
specifier|static
name|InputStream
name|getLatestFile
parameter_list|(
name|String
name|dirName
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|Collection
argument_list|<
name|File
argument_list|>
name|oldFiles
init|=
literal|null
decl_stmt|;
specifier|final
name|String
name|prefix
init|=
name|fileName
operator|+
literal|'.'
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|dirName
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
comment|// there can be a race between checking for a file and opening it...
comment|// the user may have just put a new version in and deleted an old version.
comment|// try multiple times in a row.
for|for
control|(
name|int
name|retry
init|=
literal|0
init|;
name|retry
operator|<
literal|10
operator|&&
name|is
operator|==
literal|null
condition|;
name|retry
operator|++
control|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|dirName
argument_list|)
decl_stmt|;
name|String
index|[]
name|names
init|=
name|dir
operator|.
name|list
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|names
index|[
name|names
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
name|oldFiles
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|oldFiles
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|names
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// swallow exception for now
block|}
block|}
comment|// allow exception to be thrown from the final try.
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
comment|// delete old files only after we have successfully opened the newest
if|if
condition|(
name|oldFiles
operator|!=
literal|null
condition|)
block|{
name|delete
argument_list|(
name|oldFiles
argument_list|)
expr_stmt|;
block|}
return|return
name|is
return|;
block|}
DECL|field|deleteList
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|File
argument_list|>
name|deleteList
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|delete
specifier|private
specifier|static
specifier|synchronized
name|void
name|delete
parameter_list|(
name|Collection
argument_list|<
name|File
argument_list|>
name|files
parameter_list|)
block|{
synchronized|synchronized
init|(
name|deleteList
init|)
block|{
name|deleteList
operator|.
name|addAll
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|deleted
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|df
range|:
name|deleteList
control|)
block|{
try|try
block|{
try|try
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|df
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|cause
parameter_list|)
block|{
comment|// TODO: should this class care if a file couldn't be deleted?
comment|// this just emulates previous behavior, where only SecurityException would be handled.
block|}
comment|// deleteList.remove(df);
name|deleted
operator|.
name|add
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|df
operator|.
name|exists
argument_list|()
condition|)
block|{
name|deleted
operator|.
name|add
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|deleteList
operator|.
name|removeAll
argument_list|(
name|deleted
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

