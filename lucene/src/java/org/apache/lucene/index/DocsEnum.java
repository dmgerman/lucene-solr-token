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
name|search
operator|.
name|DocIdSetIterator
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
name|AttributeSource
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
name|IntsRef
import|;
end_import

begin_comment
comment|/** Iterates through the documents, term freq and positions.  *  NOTE: you must first call {@link #nextDoc}.  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|DocsEnum
specifier|public
specifier|abstract
class|class
name|DocsEnum
extends|extends
name|DocIdSetIterator
block|{
DECL|field|atts
specifier|private
name|AttributeSource
name|atts
init|=
literal|null
decl_stmt|;
comment|/** Returns term frequency in the current document.  Do    *  not call this before {@link #nextDoc} is first called,    *  nor after {@link #nextDoc} returns NO_MORE_DOCS. */
DECL|method|freq
specifier|public
specifier|abstract
name|int
name|freq
parameter_list|()
function_decl|;
comment|/** Returns the related attributes. */
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
if|if
condition|(
name|atts
operator|==
literal|null
condition|)
name|atts
operator|=
operator|new
name|AttributeSource
argument_list|()
expr_stmt|;
return|return
name|atts
return|;
block|}
comment|// TODO: maybe add bulk read only docIDs (for eventual
comment|// match-only scoring)
DECL|class|BulkReadResult
specifier|public
specifier|static
class|class
name|BulkReadResult
block|{
DECL|field|docs
specifier|public
specifier|final
name|IntsRef
name|docs
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
DECL|field|freqs
specifier|public
specifier|final
name|IntsRef
name|freqs
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
block|}
DECL|field|bulkResult
specifier|protected
name|BulkReadResult
name|bulkResult
decl_stmt|;
DECL|method|initBulkResult
specifier|protected
specifier|final
name|void
name|initBulkResult
parameter_list|()
block|{
if|if
condition|(
name|bulkResult
operator|==
literal|null
condition|)
block|{
name|bulkResult
operator|=
operator|new
name|BulkReadResult
argument_list|()
expr_stmt|;
name|bulkResult
operator|.
name|docs
operator|.
name|ints
operator|=
operator|new
name|int
index|[
literal|64
index|]
expr_stmt|;
name|bulkResult
operator|.
name|freqs
operator|.
name|ints
operator|=
operator|new
name|int
index|[
literal|64
index|]
expr_stmt|;
block|}
block|}
DECL|method|getBulkResult
specifier|public
name|BulkReadResult
name|getBulkResult
parameter_list|()
block|{
name|initBulkResult
argument_list|()
expr_stmt|;
return|return
name|bulkResult
return|;
block|}
comment|/** Bulk read (docs and freqs).  After this is called,    * {@link #docID()} and {@link #freq} are undefined.  This    * returns the count read, or 0 if the end is reached.    * The IntsRef for docs and freqs will not have their    * length set.    *     *<p>NOTE: the default impl simply delegates to {@link    *  #nextDoc}, but subclasses may do this more    *  efficiently. */
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|final
name|int
index|[]
name|docs
init|=
name|bulkResult
operator|.
name|docs
operator|.
name|ints
decl_stmt|;
specifier|final
name|int
index|[]
name|freqs
init|=
name|bulkResult
operator|.
name|freqs
operator|.
name|ints
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|docs
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|doc
init|=
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|docs
index|[
name|count
index|]
operator|=
name|doc
expr_stmt|;
name|freqs
index|[
name|count
index|]
operator|=
name|freq
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

