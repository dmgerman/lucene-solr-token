begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
package|;
end_package

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
name|ValueSource
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
name|DocIdSet
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
name|Filter
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
name|FilteredDocIdSet
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
name|Bits
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
comment|/**  * @lucene.internal  */
end_comment

begin_class
DECL|class|ValueSourceFilter
specifier|public
class|class
name|ValueSourceFilter
extends|extends
name|Filter
block|{
DECL|field|startingFilter
specifier|final
name|Filter
name|startingFilter
decl_stmt|;
DECL|field|source
specifier|final
name|ValueSource
name|source
decl_stmt|;
DECL|field|min
specifier|final
name|double
name|min
decl_stmt|;
DECL|field|max
specifier|final
name|double
name|max
decl_stmt|;
DECL|method|ValueSourceFilter
specifier|public
name|ValueSourceFilter
parameter_list|(
name|Filter
name|startingFilter
parameter_list|,
name|ValueSource
name|source
parameter_list|,
name|double
name|min
parameter_list|,
name|double
name|max
parameter_list|)
block|{
if|if
condition|(
name|startingFilter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"please provide a non-null startingFilter; you can use QueryWrapperFilter(MatchAllDocsQuery) as a no-op filter"
argument_list|)
throw|;
block|}
name|this
operator|.
name|startingFilter
operator|=
name|startingFilter
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FunctionValues
name|values
init|=
name|source
operator|.
name|getValues
argument_list|(
literal|null
argument_list|,
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilteredDocIdSet
argument_list|(
name|startingFilter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|match
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|double
name|val
init|=
name|values
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|val
operator|>
name|min
operator|&&
name|val
operator|<
name|max
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

