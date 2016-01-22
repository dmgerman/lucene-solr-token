begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|xml
operator|.
name|CoreParser
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
name|queryparser
operator|.
name|xml
operator|.
name|TestCoreParser
import|;
end_import

begin_class
DECL|class|TestXmlQParser
specifier|public
class|class
name|TestXmlQParser
extends|extends
name|TestCoreParser
block|{
DECL|field|solrCoreParser
specifier|private
name|CoreParser
name|solrCoreParser
decl_stmt|;
annotation|@
name|Override
DECL|method|coreParser
specifier|protected
name|CoreParser
name|coreParser
parameter_list|()
block|{
if|if
condition|(
name|solrCoreParser
operator|==
literal|null
condition|)
block|{
name|solrCoreParser
operator|=
operator|new
name|SolrCoreParser
argument_list|(
name|super
operator|.
name|defaultField
argument_list|()
argument_list|,
name|super
operator|.
name|analyzer
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|solrCoreParser
return|;
block|}
comment|//public void testSomeOtherQuery() {
comment|//  Query q = parse("SomeOtherQuery.xml");
comment|//  dumpResults("SomeOtherQuery", q, ?);
comment|//}
block|}
end_class

end_unit
