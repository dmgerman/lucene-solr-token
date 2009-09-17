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

begin_comment
comment|/**  * Abstract decorator class for a DocIdSet implementation  * that provides on-demand filtering/validation  * mechanism on a given DocIdSet.  *  *<p/>  *  * Technically, this same functionality could be achieved  * with ChainedFilter (under contrib/misc), however the  * benefit of this class is it never materializes the full  * bitset for the filter.  Instead, the {@link #match}  * method is invoked on-demand, per docID visited during  * searching.  If you know few docIDs will be visited, and  * the logic behind {@link #match} is relatively costly,  * this may be a better way to filter than ChainedFilter.  *  * @see DocIdSet  */
end_comment

begin_class
DECL|class|FilteredDocIdSet
specifier|public
specifier|abstract
class|class
name|FilteredDocIdSet
extends|extends
name|DocIdSet
block|{
DECL|field|_innerSet
specifier|private
specifier|final
name|DocIdSet
name|_innerSet
decl_stmt|;
comment|/**    * Constructor.    * @param innerSet Underlying DocIdSet    */
DECL|method|FilteredDocIdSet
specifier|public
name|FilteredDocIdSet
parameter_list|(
name|DocIdSet
name|innerSet
parameter_list|)
block|{
name|_innerSet
operator|=
name|innerSet
expr_stmt|;
block|}
comment|/** This DocIdSet implementation is cacheable if the inner set is cacheable. */
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
name|_innerSet
operator|.
name|isCacheable
argument_list|()
return|;
block|}
comment|/**    * Validation method to determine whether a docid should be in the result set.    * @param docid docid to be tested    * @return true if input docid should be in the result set, false otherwise.    */
DECL|method|match
specifier|protected
specifier|abstract
name|boolean
name|match
parameter_list|(
name|int
name|docid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Implementation of the contract to build a DocIdSetIterator.    * @see DocIdSetIterator    * @see FilteredDocIdSetIterator    */
comment|// @Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FilteredDocIdSetIterator
argument_list|(
name|_innerSet
operator|.
name|iterator
argument_list|()
argument_list|)
block|{
specifier|protected
name|boolean
name|match
parameter_list|(
name|int
name|docid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FilteredDocIdSet
operator|.
name|this
operator|.
name|match
argument_list|(
name|docid
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

