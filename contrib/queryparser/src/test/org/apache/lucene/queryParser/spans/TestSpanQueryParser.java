begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|spans
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|core
operator|.
name|QueryNodeException
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
name|core
operator|.
name|nodes
operator|.
name|OrQueryNode
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
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|core
operator|.
name|parser
operator|.
name|SyntaxParser
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
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessorPipeline
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
name|original
operator|.
name|parser
operator|.
name|OriginalSyntaxParser
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
name|spans
operator|.
name|SpanOrQuery
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|SpanTermQuery
import|;
end_import

begin_comment
comment|/**  * This test case demonstrates how the new query parser can be used.<br/>  *<br/>  *   * It tests queries likes "term", "field:term" "term1 term2" "term1 OR term2",  * which are all already supported by the current syntax parser (  * {@link OriginalSyntaxParser}).<br/>  *<br/>  *   * The goals is to create a new query parser that supports only the pair  * "field:term" or a list of pairs separated or not by an OR operator, and from  * this query generate {@link SpanQuery} objects instead of the regular  * {@link Query} objects. Basically, every pair will be converted to a  * {@link SpanTermQuery} object and if there are more than one pair they will be  * grouped by an {@link OrQueryNode}.<br/>  *<br/>  *   * Another functionality that will be added is the ability to convert every  * field defined in the query to an unique specific field.<br/>  *<br/>  *   * The query generation is divided in three different steps: parsing (syntax),  * processing (semantic) and building.<br/>  *<br/>  *   * The parsing phase, as already mentioned will be performed by the current  * query parser: {@link OriginalSyntaxParser}.<br/>  *<br/>  *   * The processing phase will be performed by a processor pipeline which is  * compound by 2 processors: {@link SpansValidatorQueryNodeProcessor} and  * {@link UniqueFieldQueryNodeProcessor}.  *   *<pre>  *   *   {@link SpansValidatorQueryNodeProcessor}: as it's going to use the current   *   query parser to parse the syntax, it will support more features than we want,  *   this processor basically validates the query node tree generated by the parser  *   and just let got through the elements we want, all the other elements as   *   wildcards, range queries, etc...if found, an exception is thrown.  *     *   {@link UniqueFieldQueryNodeProcessor}: this processor will take care of reading  *   what is the&quot;unique field&quot; from the configuration and convert every field defined  *   in every pair to this&quot;unique field&quot;. For that, a {@link SpansQueryConfigHandler} is  *   used, which has the {@link UniqueFieldAttribute} defined in it.  *</pre>  *   * The building phase is performed by the {@link SpansQueryTreeBuilder}, which  * basically contains a map that defines which builder will be used to generate  * {@link SpanQuery} objects from {@link QueryNode} objects.<br/>  *<br/>  *   * @see SpansQueryConfigHandler  * @see SpansQueryTreeBuilder  * @see SpansValidatorQueryNodeProcessor  * @see SpanOrQueryNodeBuilder  * @see SpanTermQueryNodeBuilder  * @see OriginalSyntaxParser  * @see UniqueFieldQueryNodeProcessor  * @see UniqueFieldAttribute  */
end_comment

begin_class
DECL|class|TestSpanQueryParser
specifier|public
class|class
name|TestSpanQueryParser
extends|extends
name|TestCase
block|{
DECL|field|spanProcessorPipeline
specifier|private
name|QueryNodeProcessorPipeline
name|spanProcessorPipeline
decl_stmt|;
DECL|field|spanQueryConfigHandler
specifier|private
name|SpansQueryConfigHandler
name|spanQueryConfigHandler
decl_stmt|;
DECL|field|spansQueryTreeBuilder
specifier|private
name|SpansQueryTreeBuilder
name|spansQueryTreeBuilder
decl_stmt|;
DECL|field|queryParser
specifier|private
name|SyntaxParser
name|queryParser
init|=
operator|new
name|OriginalSyntaxParser
argument_list|()
decl_stmt|;
DECL|method|TestSpanQueryParser
specifier|public
name|TestSpanQueryParser
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|this
operator|.
name|spanProcessorPipeline
operator|=
operator|new
name|QueryNodeProcessorPipeline
argument_list|()
expr_stmt|;
name|this
operator|.
name|spanQueryConfigHandler
operator|=
operator|new
name|SpansQueryConfigHandler
argument_list|()
expr_stmt|;
name|this
operator|.
name|spansQueryTreeBuilder
operator|=
operator|new
name|SpansQueryTreeBuilder
argument_list|()
expr_stmt|;
comment|// set up the processor pipeline
name|this
operator|.
name|spanProcessorPipeline
operator|.
name|setQueryConfigHandler
argument_list|(
name|this
operator|.
name|spanQueryConfigHandler
argument_list|)
expr_stmt|;
name|this
operator|.
name|spanProcessorPipeline
operator|.
name|addProcessor
argument_list|(
operator|new
name|SpansValidatorQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|spanProcessorPipeline
operator|.
name|addProcessor
argument_list|(
operator|new
name|UniqueFieldQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getSpanQuery
specifier|public
name|SpanQuery
name|getSpanQuery
parameter_list|(
name|CharSequence
name|query
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|getSpanQuery
argument_list|(
literal|""
argument_list|,
name|query
argument_list|)
return|;
block|}
DECL|method|getSpanQuery
specifier|public
name|SpanQuery
name|getSpanQuery
parameter_list|(
name|CharSequence
name|uniqueField
parameter_list|,
name|CharSequence
name|query
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|UniqueFieldAttribute
name|uniqueFieldAtt
init|=
operator|(
name|UniqueFieldAttribute
operator|)
name|this
operator|.
name|spanQueryConfigHandler
operator|.
name|getAttribute
argument_list|(
name|UniqueFieldAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|uniqueFieldAtt
operator|.
name|setUniqueField
argument_list|(
name|uniqueField
argument_list|)
expr_stmt|;
name|QueryNode
name|queryTree
init|=
name|this
operator|.
name|queryParser
operator|.
name|parse
argument_list|(
name|query
argument_list|,
literal|"defaultField"
argument_list|)
decl_stmt|;
name|queryTree
operator|=
name|this
operator|.
name|spanProcessorPipeline
operator|.
name|process
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|spansQueryTreeBuilder
operator|.
name|build
argument_list|(
name|queryTree
argument_list|)
return|;
block|}
DECL|method|testTermSpans
specifier|public
name|void
name|testTermSpans
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|getSpanQuery
argument_list|(
literal|"field:term"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"term"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getSpanQuery
argument_list|(
literal|"term"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"term"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getSpanQuery
argument_list|(
literal|"field:term"
argument_list|)
operator|instanceof
name|SpanTermQuery
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getSpanQuery
argument_list|(
literal|"term"
argument_list|)
operator|instanceof
name|SpanTermQuery
argument_list|)
expr_stmt|;
block|}
DECL|method|testUniqueField
specifier|public
name|void
name|testUniqueField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|getSpanQuery
argument_list|(
literal|"field"
argument_list|,
literal|"term"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"field:term"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getSpanQuery
argument_list|(
literal|"field"
argument_list|,
literal|"field:term"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"field:term"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getSpanQuery
argument_list|(
literal|"field"
argument_list|,
literal|"anotherField:term"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"field:term"
argument_list|)
expr_stmt|;
block|}
DECL|method|testOrSpans
specifier|public
name|void
name|testOrSpans
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|getSpanQuery
argument_list|(
literal|"term1 term2"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"spanOr([term1, term2])"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getSpanQuery
argument_list|(
literal|"term1 OR term2"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"spanOr([term1, term2])"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getSpanQuery
argument_list|(
literal|"term1 term2"
argument_list|)
operator|instanceof
name|SpanOrQuery
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getSpanQuery
argument_list|(
literal|"term1 term2"
argument_list|)
operator|instanceof
name|SpanOrQuery
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueryValidator
specifier|public
name|void
name|testQueryValidator
parameter_list|()
throws|throws
name|QueryNodeException
block|{
try|try
block|{
name|getSpanQuery
argument_list|(
literal|"term*"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"QueryNodeException was expected, wildcard queries should not be supported"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryNodeException
name|ex
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|getSpanQuery
argument_list|(
literal|"[a TO z]"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"QueryNodeException was expected, range queries should not be supported"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryNodeException
name|ex
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|getSpanQuery
argument_list|(
literal|"a~0.5"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"QueryNodeException was expected, boost queries should not be supported"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryNodeException
name|ex
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|getSpanQuery
argument_list|(
literal|"a^0.5"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"QueryNodeException was expected, fuzzy queries should not be supported"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryNodeException
name|ex
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|getSpanQuery
argument_list|(
literal|"\"a b\""
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"QueryNodeException was expected, quoted queries should not be supported"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryNodeException
name|ex
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|getSpanQuery
argument_list|(
literal|"(a b)"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"QueryNodeException was expected, parenthesized queries should not be supported"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryNodeException
name|ex
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|getSpanQuery
argument_list|(
literal|"a AND b"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"QueryNodeException was expected, and queries should not be supported"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|QueryNodeException
name|ex
parameter_list|)
block|{
comment|// expected exception
block|}
block|}
block|}
end_class

end_unit

