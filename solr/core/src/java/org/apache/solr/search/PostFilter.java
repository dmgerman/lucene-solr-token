begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_comment
comment|/** The PostFilter interface provides a mechanism to further filter documents  * after they have already gone through the main query and other filters.  * This is appropriate for filters with a very high cost.  *<p>  * The filtering mechanism used is a {@link DelegatingCollector}  * that allows the filter to not call the delegate for certain documents,  * thus effectively filtering them out.  This also avoids the normal  * filter advancing mechanism which asks for the first acceptable document on  * or after the target (which is undesirable for expensive filters).  * This collector interface also enables better performance when an external system  * must be consulted, since document ids may be buffered and batched into  * a single request to the external system.  *<p>  * Implementations of this interface must also be a Query.  * If an implementation can only support the collector method of  * filtering through getFilterCollector, then ExtendedQuery.getCached()  * should always return false, and ExtendedQuery.getCost() should  * return no less than 100.  *  * @see ExtendedQueryBase  */
end_comment

begin_interface
DECL|interface|PostFilter
specifier|public
interface|interface
name|PostFilter
extends|extends
name|ExtendedQuery
block|{
comment|/** Returns a DelegatingCollector to be run after the main query and all of its filters, but before any sorting or grouping collectors */
DECL|method|getFilterCollector
specifier|public
name|DelegatingCollector
name|getFilterCollector
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

