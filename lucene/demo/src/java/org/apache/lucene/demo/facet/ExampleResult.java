begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResult
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Result of running an example program.  * This is a general object for allowing to write a test   * that runs an example and verifies its results.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|ExampleResult
specifier|public
class|class
name|ExampleResult
block|{
comment|/** Sole constructor. */
DECL|method|ExampleResult
specifier|public
name|ExampleResult
parameter_list|()
block|{}
DECL|field|facetResults
specifier|private
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
decl_stmt|;
comment|/**    * Returns the facet results    */
DECL|method|getFacetResults
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|getFacetResults
parameter_list|()
block|{
return|return
name|facetResults
return|;
block|}
comment|/**    * Sets the facet results    */
DECL|method|setFacetResults
specifier|public
name|void
name|setFacetResults
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
parameter_list|)
block|{
name|this
operator|.
name|facetResults
operator|=
name|facetResults
expr_stmt|;
block|}
block|}
end_class

end_unit

