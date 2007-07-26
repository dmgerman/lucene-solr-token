begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|document
operator|.
name|FieldSelector
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
name|store
operator|.
name|Directory
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
name|Hashtable
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
comment|/** An IndexReader which reads multiple indexes, appending their content.  *  * @version $Id$  */
end_comment

begin_class
DECL|class|MultiReader
specifier|public
class|class
name|MultiReader
extends|extends
name|MultiSegmentReader
block|{
comment|/**   *<p>Construct a MultiReader aggregating the named set of (sub)readers.   * Directory locking for delete, undeleteAll, and setNorm operations is   * left to the subreaders.</p>   *<p>Note that all subreaders are closed if this Multireader is closed.</p>   * @param subReaders set of (sub)readers   * @throws IOException   */
DECL|method|MultiReader
specifier|public
name|MultiReader
parameter_list|(
name|IndexReader
index|[]
name|subReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|subReaders
operator|.
name|length
operator|==
literal|0
condition|?
literal|null
else|:
name|subReaders
index|[
literal|0
index|]
operator|.
name|directory
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|subReaders
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks recursively if all subreaders are up to date.     */
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|subReaders
index|[
name|i
index|]
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// all subreaders are up to date
return|return
literal|true
return|;
block|}
comment|/** Not implemented.    * @throws UnsupportedOperationException    */
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"MultiReader does not support this method."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

