begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
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
name|RAMOutputStream
import|;
end_import

begin_comment
comment|/**  * This abstract class writes skip lists with multiple levels.  *   * Example for skipInterval = 3:  *                                                     c            (skip level 2)  *                 c                 c                 c            (skip level 1)   *     x     x     x     x     x     x     x     x     x     x      (skip level 0)  * d d d d d d d d d d d d d d d d d d d d d d d d d d d d d d d d  (posting list)  *     3     6     9     12    15    18    21    24    27    30     (df)  *   * d - document  * x - skip data  * c - skip data with child pointer  *   * Skip level i contains every skipInterval-th entry from skip level i-1.  * Therefore the number of entries on level i is: floor(df / ((skipInterval ^ (i + 1))).  *   * Each skip entry on a level i>0 contains a pointer to the corresponding skip entry in list i-1.  * This guarantees a logarithmic amount of skips to find the target document.  *   * While this class takes care of writing the different skip levels,  * subclasses must define the actual format of the skip data.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|MultiLevelSkipListWriter
specifier|public
specifier|abstract
class|class
name|MultiLevelSkipListWriter
block|{
comment|// number of levels in this skip list
DECL|field|numberOfSkipLevels
specifier|protected
name|int
name|numberOfSkipLevels
decl_stmt|;
comment|// the skip interval in the list with level = 0
DECL|field|skipInterval
specifier|private
name|int
name|skipInterval
decl_stmt|;
comment|// for every skip level a different buffer is used
DECL|field|skipBuffer
specifier|private
name|RAMOutputStream
index|[]
name|skipBuffer
decl_stmt|;
DECL|method|MultiLevelSkipListWriter
specifier|protected
name|MultiLevelSkipListWriter
parameter_list|(
name|int
name|skipInterval
parameter_list|,
name|int
name|maxSkipLevels
parameter_list|,
name|int
name|df
parameter_list|)
block|{
name|this
operator|.
name|skipInterval
operator|=
name|skipInterval
expr_stmt|;
comment|// calculate the maximum number of skip levels for this document frequency
name|numberOfSkipLevels
operator|=
name|df
operator|==
literal|0
condition|?
literal|0
else|:
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|df
argument_list|)
operator|/
name|Math
operator|.
name|log
argument_list|(
name|skipInterval
argument_list|)
argument_list|)
expr_stmt|;
comment|// make sure it does not exceed maxSkipLevels
if|if
condition|(
name|numberOfSkipLevels
operator|>
name|maxSkipLevels
condition|)
block|{
name|numberOfSkipLevels
operator|=
name|maxSkipLevels
expr_stmt|;
block|}
block|}
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|()
block|{
name|skipBuffer
operator|=
operator|new
name|RAMOutputStream
index|[
name|numberOfSkipLevels
index|]
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
name|numberOfSkipLevels
condition|;
name|i
operator|++
control|)
block|{
name|skipBuffer
index|[
name|i
index|]
operator|=
operator|new
name|RAMOutputStream
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|resetSkip
specifier|protected
name|void
name|resetSkip
parameter_list|()
block|{
comment|// creates new buffers or empties the existing ones
if|if
condition|(
name|skipBuffer
operator|==
literal|null
condition|)
block|{
name|init
argument_list|()
expr_stmt|;
block|}
else|else
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
name|skipBuffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|skipBuffer
index|[
name|i
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Subclasses must implement the actual skip data encoding in this method.    *      * @param level the level skip data shall be writing for    * @param skipBuffer the skip buffer to write to    */
DECL|method|writeSkipData
specifier|protected
specifier|abstract
name|void
name|writeSkipData
parameter_list|(
name|int
name|level
parameter_list|,
name|IndexOutput
name|skipBuffer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Writes the current skip data to the buffers. The current document frequency determines    * the max level is skip data is to be written to.     *     * @param df the current document frequency     * @throws IOException    */
DECL|method|bufferSkip
specifier|public
name|void
name|bufferSkip
parameter_list|(
name|int
name|df
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numLevels
decl_stmt|;
comment|// determine max level
for|for
control|(
name|numLevels
operator|=
literal|0
init|;
operator|(
name|df
operator|%
name|skipInterval
operator|)
operator|==
literal|0
operator|&&
name|numLevels
operator|<
name|numberOfSkipLevels
condition|;
name|df
operator|/=
name|skipInterval
control|)
block|{
name|numLevels
operator|++
expr_stmt|;
block|}
name|long
name|childPointer
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|level
init|=
literal|0
init|;
name|level
operator|<
name|numLevels
condition|;
name|level
operator|++
control|)
block|{
name|writeSkipData
argument_list|(
name|level
argument_list|,
name|skipBuffer
index|[
name|level
index|]
argument_list|)
expr_stmt|;
name|long
name|newChildPointer
init|=
name|skipBuffer
index|[
name|level
index|]
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|level
operator|!=
literal|0
condition|)
block|{
comment|// store child pointers for all levels except the lowest
name|skipBuffer
index|[
name|level
index|]
operator|.
name|writeVLong
argument_list|(
name|childPointer
argument_list|)
expr_stmt|;
block|}
comment|//remember the childPointer for the next level
name|childPointer
operator|=
name|newChildPointer
expr_stmt|;
block|}
block|}
comment|/**    * Writes the buffered skip lists to the given output.    *     * @param output the IndexOutput the skip lists shall be written to     * @return the pointer the skip list starts    */
DECL|method|writeSkip
specifier|public
name|long
name|writeSkip
parameter_list|(
name|IndexOutput
name|output
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|skipPointer
init|=
name|output
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
comment|//System.out.println("skipper.writeSkip fp=" + skipPointer);
if|if
condition|(
name|skipBuffer
operator|==
literal|null
operator|||
name|skipBuffer
operator|.
name|length
operator|==
literal|0
condition|)
return|return
name|skipPointer
return|;
for|for
control|(
name|int
name|level
init|=
name|numberOfSkipLevels
operator|-
literal|1
init|;
name|level
operator|>
literal|0
condition|;
name|level
operator|--
control|)
block|{
name|long
name|length
init|=
name|skipBuffer
index|[
name|level
index|]
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|output
operator|.
name|writeVLong
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|skipBuffer
index|[
name|level
index|]
operator|.
name|writeTo
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
name|skipBuffer
index|[
literal|0
index|]
operator|.
name|writeTo
argument_list|(
name|output
argument_list|)
expr_stmt|;
return|return
name|skipPointer
return|;
block|}
block|}
end_class

end_unit

