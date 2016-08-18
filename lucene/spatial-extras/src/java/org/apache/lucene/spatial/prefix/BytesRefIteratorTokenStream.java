begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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
name|tokenattributes
operator|.
name|BytesTermAttribute
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
name|BytesRef
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
name|BytesRefIterator
import|;
end_import

begin_comment
comment|/**  * A TokenStream used internally by {@link org.apache.lucene.spatial.prefix.PrefixTreeStrategy}.  *  * This is modelled after {@link org.apache.lucene.legacy.LegacyNumericTokenStream}.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|BytesRefIteratorTokenStream
class|class
name|BytesRefIteratorTokenStream
extends|extends
name|TokenStream
block|{
DECL|method|getBytesRefIterator
specifier|public
name|BytesRefIterator
name|getBytesRefIterator
parameter_list|()
block|{
return|return
name|bytesIter
return|;
block|}
DECL|method|setBytesRefIterator
specifier|public
name|BytesRefIteratorTokenStream
name|setBytesRefIterator
parameter_list|(
name|BytesRefIterator
name|iter
parameter_list|)
block|{
name|this
operator|.
name|bytesIter
operator|=
name|iter
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytesIter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call setBytesRefIterator() before usage"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytesIter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"call setBytesRefIterator() before usage"
argument_list|)
throw|;
comment|// get next
name|BytesRef
name|bytes
init|=
name|bytesIter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|bytesAtt
operator|.
name|setBytesRef
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
comment|//note: we don't bother setting posInc or type attributes.  There's no point to it.
return|return
literal|true
return|;
block|}
block|}
comment|//members
DECL|field|bytesAtt
specifier|private
specifier|final
name|BytesTermAttribute
name|bytesAtt
init|=
name|addAttribute
argument_list|(
name|BytesTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|bytesIter
specifier|private
name|BytesRefIterator
name|bytesIter
init|=
literal|null
decl_stmt|;
comment|// null means not initialized
block|}
end_class

end_unit

