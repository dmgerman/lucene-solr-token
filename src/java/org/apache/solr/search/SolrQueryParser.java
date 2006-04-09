begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|queryParser
operator|.
name|QueryParser
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
name|*
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
comment|// TODO: implement the analysis of simple fields with
end_comment

begin_comment
comment|// FieldType.toInternal() instead of going through the
end_comment

begin_comment
comment|// analyzer.  Should lead to faster query parsing.
end_comment

begin_comment
comment|/**  * @author yonik  */
end_comment

begin_class
DECL|class|SolrQueryParser
specifier|public
class|class
name|SolrQueryParser
extends|extends
name|QueryParser
block|{
DECL|field|schema
specifier|protected
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|method|SolrQueryParser
specifier|public
name|SolrQueryParser
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|String
name|defaultField
parameter_list|)
block|{
name|super
argument_list|(
name|defaultField
operator|==
literal|null
condition|?
name|schema
operator|.
name|getDefaultSearchFieldName
argument_list|()
else|:
name|defaultField
argument_list|,
name|schema
operator|.
name|getQueryAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|setLowercaseExpandedTerms
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getFieldQuery
specifier|protected
name|Query
name|getFieldQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|)
throws|throws
name|ParseException
block|{
comment|// intercept magic field name of "_" to use as a hook for our
comment|// own functions.
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"_val_"
argument_list|)
condition|)
block|{
return|return
name|QueryParsing
operator|.
name|parseFunction
argument_list|(
name|queryText
argument_list|,
name|schema
argument_list|)
return|;
block|}
comment|// default to a normal field query
return|return
name|super
operator|.
name|getFieldQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|)
return|;
block|}
DECL|method|getRangeQuery
specifier|protected
name|Query
name|getRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
throws|throws
name|ParseException
block|{
name|FieldType
name|ft
init|=
name|schema
operator|.
name|getFieldType
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstantScoreRangeQuery
argument_list|(
name|field
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|part1
argument_list|)
condition|?
literal|null
else|:
name|ft
operator|.
name|toInternal
argument_list|(
name|part1
argument_list|)
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|part2
argument_list|)
condition|?
literal|null
else|:
name|ft
operator|.
name|toInternal
argument_list|(
name|part2
argument_list|)
argument_list|,
name|inclusive
argument_list|,
name|inclusive
argument_list|)
return|;
block|}
DECL|method|getPrefixQuery
specifier|protected
name|Query
name|getPrefixQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|termStr
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|getLowercaseExpandedTerms
argument_list|()
condition|)
block|{
name|termStr
operator|=
name|termStr
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
block|}
comment|// TODO: toInternal() won't necessarily work on partial
comment|// values, so it looks like i need a getPrefix() function
comment|// on fieldtype?  Or at the minimum, a method on fieldType
comment|// that can tell me if I should lowercase or not...
comment|// Schema could tell if lowercase filter is in the chain,
comment|// but a more sure way would be to run something through
comment|// the first time and check if it got lowercased.
comment|// TODO: throw exception of field type doesn't support prefixes?
comment|// (sortable numeric types don't do prefixes, but can do range queries)
name|Term
name|t
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termStr
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstantScorePrefixQuery
argument_list|(
name|t
argument_list|)
return|;
block|}
block|}
end_class

end_unit

