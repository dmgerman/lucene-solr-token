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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|IndexInput
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
name|RAMFile
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
name|RAMInputStream
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

begin_comment
comment|/**  * Prefix codes term instances (prefixes are shared)  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PrefixCodedTerms
class|class
name|PrefixCodedTerms
implements|implements
name|Iterable
argument_list|<
name|Term
argument_list|>
block|{
DECL|field|buffer
specifier|final
name|RAMFile
name|buffer
decl_stmt|;
DECL|method|PrefixCodedTerms
specifier|private
name|PrefixCodedTerms
parameter_list|(
name|RAMFile
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
block|}
comment|/** @return size in bytes */
DECL|method|getSizeInBytes
specifier|public
name|long
name|getSizeInBytes
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|getSizeInBytes
argument_list|()
return|;
block|}
comment|/** @return iterator over the bytes */
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Term
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|PrefixCodedTermsIterator
argument_list|()
return|;
block|}
DECL|class|PrefixCodedTermsIterator
class|class
name|PrefixCodedTermsIterator
implements|implements
name|Iterator
argument_list|<
name|Term
argument_list|>
block|{
DECL|field|input
specifier|final
name|IndexInput
name|input
decl_stmt|;
DECL|field|field
name|String
name|field
init|=
literal|""
decl_stmt|;
DECL|field|bytes
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|term
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
decl_stmt|;
DECL|method|PrefixCodedTermsIterator
name|PrefixCodedTermsIterator
parameter_list|()
block|{
try|try
block|{
name|input
operator|=
operator|new
name|RAMInputStream
argument_list|(
literal|"PrefixCodedTermsIterator"
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|input
operator|.
name|getFilePointer
argument_list|()
operator|<
name|input
operator|.
name|length
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|Term
name|next
parameter_list|()
block|{
assert|assert
name|hasNext
argument_list|()
assert|;
try|try
block|{
name|int
name|code
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// new field
name|field
operator|=
name|input
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
name|int
name|prefix
init|=
name|code
operator|>>>
literal|1
decl_stmt|;
name|int
name|suffix
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|bytes
operator|.
name|grow
argument_list|(
name|prefix
operator|+
name|suffix
argument_list|)
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|prefix
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|prefix
operator|+
name|suffix
expr_stmt|;
name|term
operator|.
name|set
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|term
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
comment|/** Builds a PrefixCodedTerms: call add repeatedly, then finish. */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|buffer
specifier|private
name|RAMFile
name|buffer
init|=
operator|new
name|RAMFile
argument_list|()
decl_stmt|;
DECL|field|output
specifier|private
name|RAMOutputStream
name|output
init|=
operator|new
name|RAMOutputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
DECL|field|lastTerm
specifier|private
name|Term
name|lastTerm
init|=
operator|new
name|Term
argument_list|(
literal|""
argument_list|)
decl_stmt|;
comment|/** add a term */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
assert|assert
name|lastTerm
operator|.
name|equals
argument_list|(
operator|new
name|Term
argument_list|(
literal|""
argument_list|)
argument_list|)
operator|||
name|term
operator|.
name|compareTo
argument_list|(
name|lastTerm
argument_list|)
operator|>
literal|0
assert|;
try|try
block|{
name|int
name|prefix
init|=
name|sharedPrefix
argument_list|(
name|lastTerm
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|bytes
argument_list|)
decl_stmt|;
name|int
name|suffix
init|=
name|term
operator|.
name|bytes
operator|.
name|length
operator|-
name|prefix
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|field
operator|.
name|equals
argument_list|(
name|lastTerm
operator|.
name|field
argument_list|)
condition|)
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|prefix
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|prefix
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|term
operator|.
name|field
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|writeVInt
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|term
operator|.
name|bytes
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|bytes
operator|.
name|offset
operator|+
name|prefix
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
name|lastTerm
operator|.
name|bytes
operator|.
name|copyBytes
argument_list|(
name|term
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|lastTerm
operator|.
name|field
operator|=
name|term
operator|.
name|field
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** return finalized form */
DECL|method|finish
specifier|public
name|PrefixCodedTerms
name|finish
parameter_list|()
block|{
try|try
block|{
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|PrefixCodedTerms
argument_list|(
name|buffer
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|sharedPrefix
specifier|private
name|int
name|sharedPrefix
parameter_list|(
name|BytesRef
name|term1
parameter_list|,
name|BytesRef
name|term2
parameter_list|)
block|{
name|int
name|pos1
init|=
literal|0
decl_stmt|;
name|int
name|pos1End
init|=
name|pos1
operator|+
name|Math
operator|.
name|min
argument_list|(
name|term1
operator|.
name|length
argument_list|,
name|term2
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|pos2
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos1
operator|<
name|pos1End
condition|)
block|{
if|if
condition|(
name|term1
operator|.
name|bytes
index|[
name|term1
operator|.
name|offset
operator|+
name|pos1
index|]
operator|!=
name|term2
operator|.
name|bytes
index|[
name|term2
operator|.
name|offset
operator|+
name|pos2
index|]
condition|)
block|{
return|return
name|pos1
return|;
block|}
name|pos1
operator|++
expr_stmt|;
name|pos2
operator|++
expr_stmt|;
block|}
return|return
name|pos1
return|;
block|}
block|}
block|}
end_class

end_unit

