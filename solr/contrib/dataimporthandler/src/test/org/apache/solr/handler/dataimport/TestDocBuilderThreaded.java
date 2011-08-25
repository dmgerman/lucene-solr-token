begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test DocBuilder with "threads"  */
end_comment

begin_class
DECL|class|TestDocBuilderThreaded
specifier|public
class|class
name|TestDocBuilderThreaded
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"dataimport-solrconfig.xml"
argument_list|,
literal|"dataimport-schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp
specifier|public
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
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"worker"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"worker"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"worker"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"worker"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
literal|"select * from y"
argument_list|,
name|docs
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aDoc
range|:
name|docs
control|)
block|{
name|String
name|theWorker
init|=
operator|(
name|String
operator|)
name|aDoc
operator|.
name|get
argument_list|(
literal|"worker"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|details
init|=
name|getDetails4Worker
argument_list|(
name|theWorker
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"details: "
operator|+
name|details
argument_list|)
expr_stmt|;
name|MockDataSource
operator|.
name|setIterator
argument_list|(
name|theWorker
argument_list|,
name|details
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|DemoProcessor
operator|.
name|entitiesInitied
operator|=
literal|0
expr_stmt|;
name|DemoEvaluator
operator|.
name|evaluated
operator|=
literal|0
expr_stmt|;
name|MockDataSource
operator|.
name|clearCache
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProcessorThreaded2Entities
specifier|public
name|void
name|testProcessorThreaded2Entities
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|threaded2EntitiesWithProcessor
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"EntityProcessor.init() for child entity was called less times than the number of rows"
argument_list|,
literal|4
argument_list|,
name|DemoProcessor
operator|.
name|entitiesInitied
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProcessor2EntitiesNoThreads
specifier|public
name|void
name|testProcessor2EntitiesNoThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|twoEntitiesWithProcessor
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"EntityProcessor.init() for child entity was called less times than the number of rows"
argument_list|,
literal|4
argument_list|,
name|DemoProcessor
operator|.
name|entitiesInitied
argument_list|)
expr_stmt|;
block|}
comment|/*   * This test fails in TestEnviroment, but works in real Live   */
annotation|@
name|Test
DECL|method|testEvaluator
specifier|public
name|void
name|testEvaluator
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|twoEntitiesWithEvaluatorProcessor
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Evaluator was invoked less times than the number of rows"
argument_list|,
literal|4
argument_list|,
name|DemoEvaluator
operator|.
name|evaluated
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContinue
specifier|public
name|void
name|testContinue
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|twoEntitiesWithFailingProcessor
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// should rollback
block|}
annotation|@
name|Test
DECL|method|testContinueThreaded
specifier|public
name|void
name|testContinueThreaded
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|twoThreadedEntitiesWithFailingProcessor
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// should rollback
block|}
annotation|@
name|Test
DECL|method|testFailingTransformerContinueThreaded
specifier|public
name|void
name|testFailingTransformerContinueThreaded
parameter_list|()
throws|throws
name|Exception
block|{
name|runFullImport
argument_list|(
name|twoThreadedEntitiesWithFailingTransformer
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getDetails4Worker
specifier|private
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getDetails4Worker
parameter_list|(
name|String
name|aWorker
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|details4Worker
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|details4Worker
operator|.
name|add
argument_list|(
name|createMap
argument_list|(
literal|"author_s"
argument_list|,
literal|"Author_"
operator|+
name|aWorker
argument_list|,
literal|"title_s"
argument_list|,
literal|"Title for "
operator|+
name|aWorker
argument_list|,
literal|"text_s"
argument_list|,
literal|" Text for "
operator|+
name|aWorker
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|details4Worker
return|;
block|}
DECL|field|threaded2EntitiesWithProcessor
specifier|private
specifier|final
name|String
name|threaded2EntitiesWithProcessor
init|=
literal|"<dataConfig><dataSource type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"job\" query=\"select * from y\""
operator|+
literal|" pk=\"id\" \n"
operator|+
literal|" threads='1'\n"
operator|+
literal|">"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<entity name=\"details\" processor=\"TestDocBuilderThreaded$DemoProcessor\" \n"
operator|+
literal|"worker=\"${job.worker}\" \n"
operator|+
literal|"query=\"${job.worker}\" \n"
operator|+
literal|"transformer=\"TemplateTransformer\" "
operator|+
literal|">"
operator|+
literal|"<field column=\"author_s\" />"
operator|+
literal|"<field column=\"title_s\" />"
operator|+
literal|"<field column=\"text_s\" />"
operator|+
literal|"<field column=\"generated_id_s\" template=\"generated_${job.id}\" />"
operator|+
literal|"</entity>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|twoEntitiesWithProcessor
specifier|private
specifier|final
name|String
name|twoEntitiesWithProcessor
init|=
literal|"<dataConfig><dataSource type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"job\" query=\"select * from y\""
operator|+
literal|" pk=\"id\" \n"
operator|+
literal|">"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<entity name=\"details\" processor=\"TestDocBuilderThreaded$DemoProcessor\" \n"
operator|+
literal|"worker=\"${job.worker}\" \n"
operator|+
literal|"query=\"${job.worker}\" \n"
operator|+
literal|"transformer=\"TemplateTransformer\" "
operator|+
literal|">"
operator|+
literal|"<field column=\"author_s\" />"
operator|+
literal|"<field column=\"title_s\" />"
operator|+
literal|"<field column=\"text_s\" />"
operator|+
literal|"<field column=\"generated_id_s\" template=\"generated_${job.id}\" />"
operator|+
literal|"</entity>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|twoEntitiesWithEvaluatorProcessor
specifier|private
specifier|final
name|String
name|twoEntitiesWithEvaluatorProcessor
init|=
literal|"<dataConfig><dataSource type=\"MockDataSource\"/>\n"
operator|+
literal|"<function name=\"concat\" class=\"TestDocBuilderThreaded$DemoEvaluator\" />"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"job\" query=\"select * from y\""
operator|+
literal|" pk=\"id\" \n"
operator|+
literal|" threads=\"1\" "
operator|+
literal|">"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<entity name=\"details\" processor=\"TestDocBuilderThreaded$DemoProcessor\" \n"
operator|+
literal|"worker=\"${dataimporter.functions.concat(details.author_s, ':_:' , details.title_s, 9 )}\" \n"
operator|+
literal|"query=\"${job.worker}\" \n"
operator|+
literal|"transformer=\"TemplateTransformer\" "
operator|+
literal|">"
operator|+
literal|"<field column=\"author_s\" />"
operator|+
literal|"<field column=\"title_s\" />"
operator|+
literal|"<field column=\"text_s\" />"
operator|+
literal|"<field column=\"generated_id_s\" template=\"generated_${job.id}\" />"
operator|+
literal|"</entity>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|twoThreadedEntitiesWithFailingProcessor
specifier|private
specifier|final
name|String
name|twoThreadedEntitiesWithFailingProcessor
init|=
literal|"<dataConfig><dataSource type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"job\" processor=\"TestDocBuilderThreaded$DemoProcessor\" \n"
operator|+
literal|" threads=\"1\" "
operator|+
literal|" query=\"select * from y\""
operator|+
literal|" pk=\"id\" \n"
operator|+
literal|" worker=\"id\" \n"
operator|+
literal|" onError=\"continue\" "
operator|+
literal|">"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<entity name=\"details\" processor=\"TestDocBuilderThreaded$FailingProcessor\" \n"
operator|+
literal|"worker=\"${job.worker}\" \n"
operator|+
literal|"query=\"${job.worker}\" \n"
operator|+
literal|"transformer=\"TemplateTransformer\" "
operator|+
literal|"onError=\"continue\" "
operator|+
literal|"fail=\"yes\" "
operator|+
literal|">"
operator|+
literal|"<field column=\"author_s\" />"
operator|+
literal|"<field column=\"title_s\" />"
operator|+
literal|"<field column=\"text_s\" />"
operator|+
literal|"<field column=\"generated_id_s\" template=\"generated_${job.id}\" />"
operator|+
literal|"</entity>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|twoEntitiesWithFailingProcessor
specifier|private
specifier|final
name|String
name|twoEntitiesWithFailingProcessor
init|=
literal|"<dataConfig><dataSource type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"job\" processor=\"TestDocBuilderThreaded$DemoProcessor\" \n"
operator|+
literal|" query=\"select * from y\""
operator|+
literal|" pk=\"id\" \n"
operator|+
literal|" worker=\"id\" \n"
operator|+
literal|" onError=\"continue\" "
operator|+
literal|">"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<entity name=\"details\" processor=\"TestDocBuilderThreaded$FailingProcessor\" \n"
operator|+
literal|"worker=\"${job.worker}\" \n"
operator|+
literal|"query=\"${job.worker}\" \n"
operator|+
literal|"transformer=\"TemplateTransformer\" "
operator|+
literal|"onError=\"continue\" "
operator|+
literal|"fail=\"yes\" "
operator|+
literal|">"
operator|+
literal|"<field column=\"author_s\" />"
operator|+
literal|"<field column=\"title_s\" />"
operator|+
literal|"<field column=\"text_s\" />"
operator|+
literal|"<field column=\"generated_id_s\" template=\"generated_${job.id}\" />"
operator|+
literal|"</entity>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|field|twoThreadedEntitiesWithFailingTransformer
specifier|private
specifier|final
name|String
name|twoThreadedEntitiesWithFailingTransformer
init|=
literal|"<dataConfig><dataSource type=\"MockDataSource\"/>\n"
operator|+
literal|"<document>"
operator|+
literal|"<entity name=\"job\" processor=\"TestDocBuilderThreaded$DemoProcessor\" \n"
operator|+
literal|" threads=\"1\" "
operator|+
literal|" query=\"select * from y\""
operator|+
literal|" pk=\"id\" \n"
operator|+
literal|" worker=\"id\" \n"
operator|+
literal|" onError=\"continue\" "
operator|+
literal|">"
operator|+
literal|"<field column=\"id\" />\n"
operator|+
literal|"<entity name=\"details\" \n"
operator|+
literal|"worker=\"${job.worker}\" \n"
operator|+
literal|"query=\"${job.worker}\" \n"
operator|+
literal|"transformer=\"TestDocBuilderThreaded$FailingTransformer\" "
operator|+
literal|"onError=\"continue\" "
operator|+
literal|">"
operator|+
literal|"<field column=\"author_s\" />"
operator|+
literal|"<field column=\"title_s\" />"
operator|+
literal|"<field column=\"text_s\" />"
operator|+
literal|"<field column=\"generated_id_s\" template=\"generated_${job.id}\" />"
operator|+
literal|"</entity>"
operator|+
literal|"</entity>"
operator|+
literal|"</document>"
operator|+
literal|"</dataConfig>"
decl_stmt|;
DECL|class|DemoProcessor
specifier|public
specifier|static
class|class
name|DemoProcessor
extends|extends
name|SqlEntityProcessor
block|{
DECL|field|entitiesInitied
specifier|public
specifier|static
name|int
name|entitiesInitied
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|String
name|result
init|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
literal|"worker"
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
operator|||
name|result
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Could not resolve entity attribute"
argument_list|)
throw|;
block|}
else|else
name|entitiesInitied
operator|++
expr_stmt|;
block|}
block|}
DECL|class|FailingProcessor
specifier|public
specifier|static
class|class
name|FailingProcessor
extends|extends
name|SqlEntityProcessor
block|{
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|String
name|fail
init|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
literal|"fail"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fail
operator|!=
literal|null
operator|&&
name|fail
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"I was told to"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|FailingTransformer
specifier|public
specifier|static
class|class
name|FailingTransformer
extends|extends
name|Transformer
block|{
annotation|@
name|Override
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Always fail"
argument_list|)
throw|;
block|}
block|}
DECL|class|DemoEvaluator
specifier|public
specifier|static
class|class
name|DemoEvaluator
extends|extends
name|Evaluator
block|{
DECL|field|evaluated
specifier|public
specifier|static
name|int
name|evaluated
init|=
literal|0
decl_stmt|;
comment|/* (non-Javadoc)     * @see org.apache.solr.handler.dataimport.Evaluator#evaluate(java.lang.String, org.apache.solr.handler.dataimport.Context)     */
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|evaluate
specifier|public
name|String
name|evaluate
parameter_list|(
name|String
name|expression
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|List
name|allParams
init|=
name|EvaluatorBag
operator|.
name|parseParams
argument_list|(
name|expression
argument_list|,
name|context
operator|.
name|getVariableResolver
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|aVar
range|:
name|allParams
control|)
block|{
name|result
operator|.
name|append
argument_list|(
name|aVar
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|evaluated
operator|++
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

