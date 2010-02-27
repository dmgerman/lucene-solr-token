begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A document in the instantiated index object graph, optionally coupled to the vector space view.   *  * @see org.apache.lucene.document.Document  */
end_comment

begin_class
DECL|class|InstantiatedDocument
specifier|public
class|class
name|InstantiatedDocument
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1l
decl_stmt|;
DECL|field|document
specifier|private
name|Document
name|document
decl_stmt|;
DECL|method|InstantiatedDocument
specifier|public
name|InstantiatedDocument
parameter_list|()
block|{
name|this
operator|.
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
block|}
DECL|method|InstantiatedDocument
specifier|public
name|InstantiatedDocument
parameter_list|(
name|Document
name|document
parameter_list|)
block|{
name|this
operator|.
name|document
operator|=
name|document
expr_stmt|;
block|}
comment|/** this is the unsafe index order document number. */
DECL|field|documentNumber
specifier|private
name|Integer
name|documentNumber
decl_stmt|;
comment|/** this is the term vector space view */
DECL|field|vectorSpace
specifier|private
name|Map
argument_list|<
name|String
comment|/*field name*/
argument_list|,
name|List
argument_list|<
name|InstantiatedTermDocumentInformation
argument_list|>
argument_list|>
name|vectorSpace
decl_stmt|;
comment|/**    * @return position of document in the index.    */
DECL|method|getDocumentNumber
specifier|public
name|Integer
name|getDocumentNumber
parameter_list|()
block|{
return|return
name|documentNumber
return|;
block|}
DECL|method|setDocumentNumber
name|void
name|setDocumentNumber
parameter_list|(
name|Integer
name|documentNumber
parameter_list|)
block|{
name|this
operator|.
name|documentNumber
operator|=
name|documentNumber
expr_stmt|;
block|}
DECL|method|getVectorSpace
specifier|public
name|Map
argument_list|<
comment|/*field name*/
name|String
argument_list|,
name|List
argument_list|<
name|InstantiatedTermDocumentInformation
argument_list|>
argument_list|>
name|getVectorSpace
parameter_list|()
block|{
return|return
name|vectorSpace
return|;
block|}
DECL|method|setVectorSpace
specifier|public
name|void
name|setVectorSpace
parameter_list|(
name|Map
argument_list|<
comment|/*field name*/
name|String
argument_list|,
name|List
argument_list|<
name|InstantiatedTermDocumentInformation
argument_list|>
argument_list|>
name|vectorSpace
parameter_list|)
block|{
name|this
operator|.
name|vectorSpace
operator|=
name|vectorSpace
expr_stmt|;
block|}
DECL|method|getDocument
specifier|public
name|Document
name|getDocument
parameter_list|()
block|{
return|return
name|document
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|document
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

