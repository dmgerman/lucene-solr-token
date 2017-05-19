begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

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
name|AbstractBadConfigTestBase
import|;
end_import

begin_class
DECL|class|BadIndexSchemaTest
specifier|public
class|class
name|BadIndexSchemaTest
extends|extends
name|AbstractBadConfigTestBase
block|{
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
specifier|final
name|String
name|schema
parameter_list|,
specifier|final
name|String
name|errString
parameter_list|)
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
name|schema
argument_list|,
name|errString
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForInvalidFieldOptions
specifier|public
name|void
name|testSevereErrorsForInvalidFieldOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-not-indexed-but-norms.xml"
argument_list|,
literal|"bad_field"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-not-indexed-but-tf.xml"
argument_list|,
literal|"bad_field"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-not-indexed-but-pos.xml"
argument_list|,
literal|"bad_field"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-omit-tf-but-not-pos.xml"
argument_list|,
literal|"bad_field"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForDuplicateFields
specifier|public
name|void
name|testSevereErrorsForDuplicateFields
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-dup-field.xml"
argument_list|,
literal|"fAgain"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForDuplicateDynamicField
specifier|public
name|void
name|testSevereErrorsForDuplicateDynamicField
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-dup-dynamicField.xml"
argument_list|,
literal|"_twice"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForUnsupportedAttributesOnDynamicField
specifier|public
name|void
name|testSevereErrorsForUnsupportedAttributesOnDynamicField
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-dynamicfield-default-val.xml"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-dynamicfield-required.xml"
argument_list|,
literal|"required"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForDuplicateFieldType
specifier|public
name|void
name|testSevereErrorsForDuplicateFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-dup-fieldType.xml"
argument_list|,
literal|"ftAgain"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForUnexpectedAnalyzer
specifier|public
name|void
name|testSevereErrorsForUnexpectedAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-nontext-analyzer.xml"
argument_list|,
literal|"StrField (bad_type)"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-analyzer-class-and-nested.xml"
argument_list|,
literal|"bad_type"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBadExternalFileField
specifier|public
name|void
name|testBadExternalFileField
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-external-filefield.xml"
argument_list|,
literal|"Only float (TrieFloatField) is currently supported as external field type."
argument_list|)
expr_stmt|;
block|}
DECL|method|testUniqueKeyRules
specifier|public
name|void
name|testUniqueKeyRules
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-uniquekey-is-copyfield-dest.xml"
argument_list|,
literal|"can not be the dest of a copyField"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-uniquekey-uses-default.xml"
argument_list|,
literal|"can not be configured with a default value"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-uniquekey-multivalued.xml"
argument_list|,
literal|"can not be configured to be multivalued"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultivaluedCurrency
specifier|public
name|void
name|testMultivaluedCurrency
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-currency-ft-multivalued.xml"
argument_list|,
literal|"types can not be multiValued: currency"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-currency-multivalued.xml"
argument_list|,
literal|"Fields can not be multiValued: money"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-currency-dynamic-multivalued.xml"
argument_list|,
literal|"Fields can not be multiValued: *_c"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCurrencyOERNoRates
specifier|public
name|void
name|testCurrencyOERNoRates
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-currency-ft-oer-norates.xml"
argument_list|,
literal|"ratesFileLocation"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCurrencyBogusCode
specifier|public
name|void
name|testCurrencyBogusCode
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-currency-ft-bogus-default-code.xml"
argument_list|,
literal|"HOSS"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-currency-ft-bogus-code-in-xml.xml"
argument_list|,
literal|"HOSS"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPerFieldtypeSimButNoSchemaSimFactory
specifier|public
name|void
name|testPerFieldtypeSimButNoSchemaSimFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-sim-global-vs-ft-mismatch.xml"
argument_list|,
literal|"global similarity does not support it"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPerFieldtypePostingsFormatButNoSchemaCodecFactory
specifier|public
name|void
name|testPerFieldtypePostingsFormatButNoSchemaCodecFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-codec-global-vs-ft-mismatch.xml"
argument_list|,
literal|"codec does not support"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocValuesUnsupported
specifier|public
name|void
name|testDocValuesUnsupported
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-unsupported-docValues.xml"
argument_list|,
literal|"does not support doc values"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSweetSpotSimBadConfig
specifier|public
name|void
name|testSweetSpotSimBadConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-sweetspot-both-tf.xml"
argument_list|,
literal|"Can not mix"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-sweetspot-partial-baseline.xml"
argument_list|,
literal|"Overriding default baselineTf"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-sweetspot-partial-hyperbolic.xml"
argument_list|,
literal|"Overriding default hyperbolicTf"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-sweetspot-partial-norms.xml"
argument_list|,
literal|"Overriding default lengthNorm"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBogusParameters
specifier|public
name|void
name|testBogusParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-bogus-field-parameters.xml"
argument_list|,
literal|"Invalid field property"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBogusAnalysisParameters
specifier|public
name|void
name|testBogusAnalysisParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-bogus-analysis-parameters.xml"
argument_list|,
literal|"Unknown parameters"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimDefaultFieldTypeHasNoExplicitSim
specifier|public
name|void
name|testSimDefaultFieldTypeHasNoExplicitSim
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-sim-default-has-no-explicit-sim.xml"
argument_list|,
literal|"ft-has-no-sim"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimDefaultFieldTypeDoesNotExist
specifier|public
name|void
name|testSimDefaultFieldTypeDoesNotExist
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-sim-default-does-not-exist.xml"
argument_list|,
literal|"ft-does-not-exist"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultOperatorBanned
specifier|public
name|void
name|testDefaultOperatorBanned
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-default-operator.xml"
argument_list|,
literal|"default operator in schema (solrQueryParser/@defaultOperator) not supported"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSchemaWithDefaultSearchField
specifier|public
name|void
name|testSchemaWithDefaultSearchField
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-defaultsearchfield.xml"
argument_list|,
literal|"Setting defaultSearchField in schema not supported since Solr 7"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

