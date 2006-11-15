begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
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
name|IOException
import|;
end_import

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|AbstractBenchmarker
specifier|public
specifier|abstract
class|class
name|AbstractBenchmarker
implements|implements
name|Benchmarker
block|{
comment|/**      * Delete files and directories, even if non-empty.      *      * @param dir file or directory      * @return true on success, false if no or part of files have been deleted      * @throws java.io.IOException      */
DECL|method|fullyDelete
specifier|public
specifier|static
name|boolean
name|fullyDelete
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dir
operator|==
literal|null
operator|||
operator|!
name|dir
operator|.
name|exists
argument_list|()
condition|)
return|return
literal|false
return|;
name|File
name|contents
index|[]
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|contents
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|contents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|contents
index|[
name|i
index|]
operator|.
name|isFile
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|contents
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|fullyDelete
argument_list|(
name|contents
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
return|return
name|dir
operator|.
name|delete
argument_list|()
return|;
block|}
block|}
end_class

end_unit

