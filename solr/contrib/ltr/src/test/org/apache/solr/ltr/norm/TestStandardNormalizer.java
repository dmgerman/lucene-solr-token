begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr.norm
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
operator|.
name|norm
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrResourceLoader
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

begin_class
DECL|class|TestStandardNormalizer
specifier|public
class|class
name|TestStandardNormalizer
block|{
DECL|field|solrResourceLoader
specifier|private
specifier|final
name|SolrResourceLoader
name|solrResourceLoader
init|=
operator|new
name|SolrResourceLoader
argument_list|()
decl_stmt|;
DECL|method|implTestStandard
specifier|private
name|Normalizer
name|implTestStandard
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|,
name|float
name|expectedAvg
parameter_list|,
name|float
name|expectedStd
parameter_list|)
block|{
specifier|final
name|Normalizer
name|n
init|=
name|Normalizer
operator|.
name|getInstance
argument_list|(
name|solrResourceLoader
argument_list|,
name|StandardNormalizer
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n
operator|instanceof
name|StandardNormalizer
argument_list|)
expr_stmt|;
specifier|final
name|StandardNormalizer
name|sn
init|=
operator|(
name|StandardNormalizer
operator|)
name|n
decl_stmt|;
name|assertEquals
argument_list|(
name|sn
operator|.
name|getAvg
argument_list|()
argument_list|,
name|expectedAvg
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sn
operator|.
name|getStd
argument_list|()
argument_list|,
name|expectedStd
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{avg=\""
operator|+
name|expectedAvg
operator|+
literal|"\", std=\""
operator|+
name|expectedStd
operator|+
literal|"\"}"
argument_list|,
name|sn
operator|.
name|paramsToMap
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|n
return|;
block|}
annotation|@
name|Test
DECL|method|testNormalizerNoParams
specifier|public
name|void
name|testNormalizerNoParams
parameter_list|()
block|{
name|implTestStandard
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
literal|0.0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidSTD
specifier|public
name|void
name|testInvalidSTD
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"std"
argument_list|,
literal|"0f"
argument_list|)
expr_stmt|;
specifier|final
name|NormalizerException
name|expectedException
init|=
operator|new
name|NormalizerException
argument_list|(
literal|"Standard Normalizer standard deviation must be positive "
operator|+
literal|"| avg = 0.0,std = 0.0"
argument_list|)
decl_stmt|;
try|try
block|{
name|implTestStandard
argument_list|(
name|params
argument_list|,
literal|0.0f
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testInvalidSTD failed to throw exception: "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NormalizerException
name|actualException
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|actualException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidSTD2
specifier|public
name|void
name|testInvalidSTD2
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"std"
argument_list|,
literal|"-1f"
argument_list|)
expr_stmt|;
specifier|final
name|NormalizerException
name|expectedException
init|=
operator|new
name|NormalizerException
argument_list|(
literal|"Standard Normalizer standard deviation must be positive "
operator|+
literal|"| avg = 0.0,std = -1.0"
argument_list|)
decl_stmt|;
try|try
block|{
name|implTestStandard
argument_list|(
name|params
argument_list|,
literal|0.0f
argument_list|,
operator|-
literal|1f
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testInvalidSTD2 failed to throw exception: "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NormalizerException
name|actualException
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|actualException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidSTD3
specifier|public
name|void
name|testInvalidSTD3
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"avg"
argument_list|,
literal|"1f"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"std"
argument_list|,
literal|"0f"
argument_list|)
expr_stmt|;
specifier|final
name|NormalizerException
name|expectedException
init|=
operator|new
name|NormalizerException
argument_list|(
literal|"Standard Normalizer standard deviation must be positive "
operator|+
literal|"| avg = 1.0,std = 0.0"
argument_list|)
decl_stmt|;
try|try
block|{
name|implTestStandard
argument_list|(
name|params
argument_list|,
literal|1f
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testInvalidSTD3 failed to throw exception: "
operator|+
name|expectedException
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NormalizerException
name|actualException
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedException
operator|.
name|toString
argument_list|()
argument_list|,
name|actualException
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNormalizer
specifier|public
name|void
name|testNormalizer
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"avg"
argument_list|,
literal|"0f"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"std"
argument_list|,
literal|"1f"
argument_list|)
expr_stmt|;
specifier|final
name|Normalizer
name|identity
init|=
name|implTestStandard
argument_list|(
name|params
argument_list|,
literal|0f
argument_list|,
literal|1f
argument_list|)
decl_stmt|;
name|float
name|value
init|=
literal|8
decl_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|identity
operator|.
name|normalize
argument_list|(
name|value
argument_list|)
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|value
operator|=
literal|150
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|identity
operator|.
name|normalize
argument_list|(
name|value
argument_list|)
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"avg"
argument_list|,
literal|"10f"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"std"
argument_list|,
literal|"1.5f"
argument_list|)
expr_stmt|;
specifier|final
name|Normalizer
name|norm
init|=
name|Normalizer
operator|.
name|getInstance
argument_list|(
name|solrResourceLoader
argument_list|,
name|StandardNormalizer
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|params
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|float
name|v
range|:
operator|new
name|float
index|[]
block|{
literal|10f
block|,
literal|20f
block|,
literal|25f
block|,
literal|30f
block|,
literal|31f
block|,
literal|40f
block|,
literal|42f
block|,
literal|100f
block|,
literal|10000000f
block|}
control|)
block|{
name|assertEquals
argument_list|(
operator|(
name|v
operator|-
literal|10f
operator|)
operator|/
operator|(
literal|1.5f
operator|)
argument_list|,
name|norm
operator|.
name|normalize
argument_list|(
name|v
argument_list|)
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

