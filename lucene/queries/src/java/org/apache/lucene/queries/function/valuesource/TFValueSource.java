begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|valuesource
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
name|Map
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
name|index
operator|.
name|PostingsEnum
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
name|index
operator|.
name|Fields
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
name|index
operator|.
name|LeafReaderContext
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
name|index
operator|.
name|Terms
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
name|index
operator|.
name|TermsEnum
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|FloatDocValues
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
name|search
operator|.
name|IndexSearcher
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
name|similarities
operator|.
name|TFIDFSimilarity
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
comment|/**   * Function that returns {@link TFIDFSimilarity#tf(float)}  * for every document.  *<p>  * Note that the configured Similarity for the field must be  * a subclass of {@link TFIDFSimilarity}  * @lucene.internal */
end_comment

begin_class
DECL|class|TFValueSource
specifier|public
class|class
name|TFValueSource
extends|extends
name|TermFreqValueSource
block|{
DECL|method|TFValueSource
specifier|public
name|TFValueSource
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|val
parameter_list|,
name|String
name|indexedField
parameter_list|,
name|BytesRef
name|indexedBytes
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|val
argument_list|,
name|indexedField
argument_list|,
name|indexedBytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"tf"
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|fields
init|=
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|fields
argument_list|()
decl_stmt|;
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|indexedField
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|(
name|IndexSearcher
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
decl_stmt|;
specifier|final
name|TFIDFSimilarity
name|similarity
init|=
name|IDFValueSource
operator|.
name|asTFIDF
argument_list|(
name|searcher
operator|.
name|getSimilarity
argument_list|(
literal|true
argument_list|)
argument_list|,
name|indexedField
argument_list|)
decl_stmt|;
if|if
condition|(
name|similarity
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"requires a TFIDFSimilarity (such as DefaultSimilarity)"
argument_list|)
throw|;
block|}
return|return
operator|new
name|FloatDocValues
argument_list|(
name|this
argument_list|)
block|{
name|PostingsEnum
name|docs
decl_stmt|;
name|int
name|atDoc
decl_stmt|;
name|int
name|lastDocRequested
init|=
operator|-
literal|1
decl_stmt|;
block|{
name|reset
parameter_list|()
constructor_decl|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
comment|// no one should call us for deleted docs?
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|indexedBytes
argument_list|)
condition|)
block|{
name|docs
operator|=
name|termsEnum
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|docs
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|docs
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|docs
operator|==
literal|null
condition|)
block|{
name|docs
operator|=
operator|new
name|PostingsEnum
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
expr_stmt|;
block|}
name|atDoc
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|doc
operator|<
name|lastDocRequested
condition|)
block|{
comment|// out-of-order access.... reset
name|reset
argument_list|()
expr_stmt|;
block|}
name|lastDocRequested
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|atDoc
operator|<
name|doc
condition|)
block|{
name|atDoc
operator|=
name|docs
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|atDoc
operator|>
name|doc
condition|)
block|{
comment|// term doesn't match this document... either because we hit the
comment|// end, or because the next doc is after this doc.
return|return
name|similarity
operator|.
name|tf
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|// a match!
return|return
name|similarity
operator|.
name|tf
argument_list|(
name|docs
operator|.
name|freq
argument_list|()
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
literal|"caught exception in function "
operator|+
name|description
argument_list|()
operator|+
literal|" : doc="
operator|+
name|doc
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

