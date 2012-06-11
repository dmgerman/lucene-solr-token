begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|Comparator
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
name|AtomicReaderContext
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
name|IndexReader
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
name|IndexReaderContext
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
name|Term
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
name|TermContext
import|;
end_import

begin_class
DECL|class|TermCollectingRewrite
specifier|abstract
class|class
name|TermCollectingRewrite
parameter_list|<
name|Q
extends|extends
name|Query
parameter_list|>
extends|extends
name|MultiTermQuery
operator|.
name|RewriteMethod
block|{
comment|/** Return a suitable top-level Query for holding all expanded terms. */
DECL|method|getTopLevelQuery
specifier|protected
specifier|abstract
name|Q
name|getTopLevelQuery
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Add a MultiTermQuery term to the top-level query */
DECL|method|addClause
specifier|protected
specifier|final
name|void
name|addClause
parameter_list|(
name|Q
name|topLevel
parameter_list|,
name|Term
name|term
parameter_list|,
name|int
name|docCount
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|addClause
argument_list|(
name|topLevel
argument_list|,
name|term
argument_list|,
name|docCount
argument_list|,
name|boost
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|addClause
specifier|protected
specifier|abstract
name|void
name|addClause
parameter_list|(
name|Q
name|topLevel
parameter_list|,
name|Term
name|term
parameter_list|,
name|int
name|docCount
parameter_list|,
name|float
name|boost
parameter_list|,
name|TermContext
name|states
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|collectTerms
specifier|final
name|void
name|collectTerms
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|,
name|TermCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReaderContext
name|topReaderContext
init|=
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|lastTermComp
init|=
literal|null
decl_stmt|;
specifier|final
name|AtomicReaderContext
index|[]
name|leaves
init|=
name|topReaderContext
operator|.
name|leaves
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|leaves
control|)
block|{
specifier|final
name|Fields
name|fields
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
comment|// reader has no fields
continue|continue;
block|}
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|query
operator|.
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
comment|// field does not exist
continue|continue;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|getTermsEnum
argument_list|(
name|query
argument_list|,
name|terms
argument_list|,
name|collector
operator|.
name|attributes
argument_list|)
decl_stmt|;
assert|assert
name|termsEnum
operator|!=
literal|null
assert|;
if|if
condition|(
name|termsEnum
operator|==
name|TermsEnum
operator|.
name|EMPTY
condition|)
continue|continue;
comment|// Check comparator compatibility:
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|newTermComp
init|=
name|termsEnum
operator|.
name|getComparator
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastTermComp
operator|!=
literal|null
operator|&&
name|newTermComp
operator|!=
literal|null
operator|&&
name|newTermComp
operator|!=
name|lastTermComp
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"term comparator should not change between segments: "
operator|+
name|lastTermComp
operator|+
literal|" != "
operator|+
name|newTermComp
argument_list|)
throw|;
name|lastTermComp
operator|=
name|newTermComp
expr_stmt|;
name|collector
operator|.
name|setReaderContext
argument_list|(
name|topReaderContext
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|collector
operator|.
name|setNextEnum
argument_list|(
name|termsEnum
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
decl_stmt|;
while|while
condition|(
operator|(
name|bytes
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|collector
operator|.
name|collect
argument_list|(
name|bytes
argument_list|)
condition|)
return|return;
comment|// interrupt whole term collection, so also don't iterate other subReaders
block|}
block|}
block|}
DECL|class|TermCollector
specifier|static
specifier|abstract
class|class
name|TermCollector
block|{
DECL|field|readerContext
specifier|protected
name|AtomicReaderContext
name|readerContext
decl_stmt|;
DECL|field|topReaderContext
specifier|protected
name|IndexReaderContext
name|topReaderContext
decl_stmt|;
DECL|method|setReaderContext
specifier|public
name|void
name|setReaderContext
parameter_list|(
name|IndexReaderContext
name|topReaderContext
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
block|{
name|this
operator|.
name|readerContext
operator|=
name|readerContext
expr_stmt|;
name|this
operator|.
name|topReaderContext
operator|=
name|topReaderContext
expr_stmt|;
block|}
comment|/** attributes used for communication with the enum */
DECL|field|attributes
specifier|public
specifier|final
name|AttributeSource
name|attributes
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
comment|/** return false to stop collecting */
DECL|method|collect
specifier|public
specifier|abstract
name|boolean
name|collect
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** the next segment's {@link TermsEnum} that is used to collect terms */
DECL|method|setNextEnum
specifier|public
specifier|abstract
name|void
name|setNextEnum
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_class

end_unit

