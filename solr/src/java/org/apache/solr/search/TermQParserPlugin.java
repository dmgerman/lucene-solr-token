begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|queryParser
operator|.
name|ParseException
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
name|Query
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
name|TermQuery
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|request
operator|.
name|SolrQueryRequest
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
name|FieldType
import|;
end_import

begin_comment
comment|/**  * Create a single term query from the input value equivalent to readableToIndexed().  * This is useful for generating filter queries from the external human readable terms returned by the  * faceting or terms components.  *  *<p>  * For text fields, no analysis is done since raw terms are already returned from the faceting  * and terms components, and not all text analysis is idempotent.  * To apply analysis to text fields as well, see the {@link FieldQParserPlugin}.  *<br>  * If no analysis or transformation is desired for any type of field, see the {@link RawQParserPlugin}.  *  *<p>Other parameters:<code>f</code>, the field  *<br>Example:<code>{!term f=weight}1.5</code>  */
end_comment

begin_class
DECL|class|TermQParserPlugin
specifier|public
class|class
name|TermQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
name|String
name|NAME
init|=
literal|"term"
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|new
name|QParser
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
name|fname
init|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|F
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldTypeNoEx
argument_list|(
name|fname
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
decl_stmt|;
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
if|if
condition|(
name|ft
operator|!=
literal|null
condition|)
block|{
name|ft
operator|.
name|readableToIndexed
argument_list|(
name|val
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|term
operator|.
name|copy
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fname
argument_list|,
name|term
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

