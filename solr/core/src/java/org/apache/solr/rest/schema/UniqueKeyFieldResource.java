begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.rest.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|GETable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|representation
operator|.
name|Representation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|resource
operator|.
name|ResourceException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This class responds to requests at /solr/(corename)/schema/uniquekey  */
end_comment

begin_class
DECL|class|UniqueKeyFieldResource
specifier|public
class|class
name|UniqueKeyFieldResource
extends|extends
name|BaseSchemaResource
implements|implements
name|GETable
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UniqueKeyFieldResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|UniqueKeyFieldResource
specifier|public
name|UniqueKeyFieldResource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doInit
specifier|public
name|void
name|doInit
parameter_list|()
throws|throws
name|ResourceException
block|{
name|super
operator|.
name|doInit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Representation
name|get
parameter_list|()
block|{
try|try
block|{
name|getSolrResponse
argument_list|()
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|UNIQUE_KEY
argument_list|,
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getSolrResponse
argument_list|()
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|handlePostExecution
argument_list|(
name|log
argument_list|)
expr_stmt|;
return|return
operator|new
name|SolrOutputRepresentation
argument_list|()
return|;
block|}
block|}
end_class

end_unit

