#include <Fuzzy.h>

Fuzzy *fuzzy = new Fuzzy();

// FuzzySet Input Suhu
FuzzySet *suhuDingin = new FuzzySet(0, 13.5, 13.5, 27);
FuzzySet *suhuNormal = new FuzzySet(25, 28, 28, 31);
FuzzySet *suhuPanas = new FuzzySet(29, 34.5, 34.5, 40);

// FuzzySet Input Kecepatan Udara
FuzzySet *kecepatanPelan = new FuzzySet(0, 2, 2, 4);
FuzzySet *kecepatanSedang = new FuzzySet(2, 5, 5, 8);
FuzzySet *kecepatanKencang = new FuzzySet(6, 10.5, 10.5, 15);

// FuzzySet Input Kelembaban
FuzzySet *kelembabanKering = new FuzzySet(50, 70, 75, 80);
FuzzySet *kelembabanSedang = new FuzzySet(75, 82.5, 82.5, 90);
FuzzySet *kelembabanBasah = new FuzzySet(85, 90, 95, 100);

// FuzzySet Output Prediksi Cuaca
FuzzySet *cerahBerawan = new FuzzySet(0, 2.5, 2.5, 5);
FuzzySet *hujanRingan = new FuzzySet(2.5, 11.25, 11.25, 20);
FuzzySet *hujanSedang = new FuzzySet(15, 32.5, 32.5, 50);
FuzzySet *hujanLebat = new FuzzySet(45, 72.5, 72.5, 100);

void inisialisasiFuzzyInputOutput()
{
  // FuzzyInput suhu
  FuzzyInput *temperature = new FuzzyInput(1);
  temperature->addFuzzySet(suhuDingin);
  temperature->addFuzzySet(suhuNormal);
  temperature->addFuzzySet(suhuPanas);
  fuzzy->addFuzzyInput(temperature);
    
  // FuzzyInput kecepatan angin
  FuzzyInput *windSpeed = new FuzzyInput(2);
  windSpeed->addFuzzySet(kecepatanPelan);
  windSpeed->addFuzzySet(kecepatanSedang);
  windSpeed->addFuzzySet(kecepatanKencang);
  fuzzy->addFuzzyInput(windSpeed);
    
  // FuzzyInput kelembaban
  FuzzyInput *humidity = new FuzzyInput(3);
  humidity->addFuzzySet(kelembabanKering);
  humidity->addFuzzySet(kelembabanSedang);
  humidity->addFuzzySet(kelembabanBasah);
  fuzzy->addFuzzyInput(humidity);

  // ===================================================

  // FuzzyOutput kondisi cuaca
  FuzzyOutput *prediksiCuaca = new FuzzyOutput(1);
  prediksiCuaca->addFuzzySet(cerahBerawan);
  prediksiCuaca->addFuzzySet(hujanRingan);
  prediksiCuaca->addFuzzySet(hujanSedang);
  prediksiCuaca->addFuzzySet(hujanLebat);
  fuzzy->addFuzzyOutput(prediksiCuaca);
}

void inisialisasiFuzzyRule()
{
  FuzzyRuleAntecedent *dingin_pelan = new FuzzyRuleAntecedent();
  dingin_pelan->joinWithAND(suhuDingin, kecepatanPelan);

  FuzzyRuleAntecedent *dingin_sedang = new FuzzyRuleAntecedent();
  dingin_sedang->joinWithAND(suhuDingin, kecepatanSedang);

  FuzzyRuleAntecedent *dingin_kencang = new FuzzyRuleAntecedent();
  dingin_kencang->joinWithAND(suhuDingin, kecepatanKencang);

  FuzzyRuleAntecedent *normal_pelan = new FuzzyRuleAntecedent();
  normal_pelan->joinWithAND(suhuNormal, kecepatanPelan);

  FuzzyRuleAntecedent *normal_sedang = new FuzzyRuleAntecedent();
  normal_sedang->joinWithAND(suhuNormal, kecepatanSedang);

  FuzzyRuleAntecedent *normal_kencang = new FuzzyRuleAntecedent();
  normal_kencang->joinWithAND(suhuNormal, kecepatanKencang);

  FuzzyRuleAntecedent *panas_pelan = new FuzzyRuleAntecedent();
  panas_pelan->joinWithAND(suhuPanas, kecepatanPelan);

  FuzzyRuleAntecedent *panas_sedang = new FuzzyRuleAntecedent();
  panas_sedang->joinWithAND(suhuPanas, kecepatanSedang);

  FuzzyRuleAntecedent *panas_kencang = new FuzzyRuleAntecedent();
  panas_kencang->joinWithAND(suhuPanas, kecepatanKencang);

  FuzzyRuleAntecedent *basah = new FuzzyRuleAntecedent();
  basah->joinSingle(kelembabanBasah);

  FuzzyRuleAntecedent *sedang = new FuzzyRuleAntecedent();
  sedang->joinSingle(kelembabanSedang);

  FuzzyRuleAntecedent *kering = new FuzzyRuleAntecedent();
  kering->joinSingle(kelembabanKering);

  FuzzyRuleConsequent *hujan_ringan = new FuzzyRuleConsequent();
  hujan_ringan->addOutput(hujanRingan);

  FuzzyRuleConsequent *hujan_sedang = new FuzzyRuleConsequent();
  hujan_sedang->addOutput(hujanSedang);

  FuzzyRuleConsequent *hujan_lebat = new FuzzyRuleConsequent();
  hujan_lebat->addOutput(hujanLebat);

  FuzzyRuleConsequent *cerah_berawan = new FuzzyRuleConsequent();
  cerah_berawan->addOutput(cerahBerawan);
  
  // Rule 1

  FuzzyRuleAntecedent *dingin_pelan_basah_1 = new FuzzyRuleAntecedent();
  dingin_pelan_basah_1->joinWithAND(dingin_pelan, basah);  

  FuzzyRule *fuzzyRule01 = new FuzzyRule(1, dingin_pelan_basah_1, hujan_ringan);
  fuzzy->addFuzzyRule(fuzzyRule01);

  // Rule 2
 
  FuzzyRuleAntecedent *dingin_pelan_sedang_2 = new FuzzyRuleAntecedent();
  dingin_pelan_sedang_2->joinWithAND(dingin_pelan, sedang);  

  FuzzyRule *fuzzyRule02 = new FuzzyRule(2, dingin_pelan_sedang_2, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule02);

  // Rule 3
 
  FuzzyRuleAntecedent *dingin_pelan_kering_3 = new FuzzyRuleAntecedent();
  dingin_pelan_kering_3->joinWithAND(dingin_pelan, kering);  

  FuzzyRule *fuzzyRule03 = new FuzzyRule(3, dingin_pelan_kering_3, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule03);

  // Rule 4
  
  FuzzyRuleAntecedent *dingin_sedang_basah_4 = new FuzzyRuleAntecedent();
  dingin_sedang_basah_4->joinWithAND(dingin_sedang, basah);  

  FuzzyRule *fuzzyRule04 = new FuzzyRule(4, dingin_sedang_basah_4, hujan_sedang);
  fuzzy->addFuzzyRule(fuzzyRule04);

  // Rule 5
  
  FuzzyRuleAntecedent *dingin_sedang_sedang_5 = new FuzzyRuleAntecedent();
  dingin_sedang_sedang_5->joinWithAND(dingin_sedang, sedang);  

  FuzzyRule *fuzzyRule05 = new FuzzyRule(5, dingin_sedang_sedang_5, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule05);

  // Rule 6

  FuzzyRuleAntecedent *dingin_sedang_kering_6 = new FuzzyRuleAntecedent();
  dingin_sedang_kering_6->joinWithAND(dingin_sedang, kering);  
  
  FuzzyRule *fuzzyRule06 = new FuzzyRule(6, dingin_sedang_kering_6, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule06);

  // Rule 7
  
  FuzzyRuleAntecedent *dingin_kencang_basah_7 = new FuzzyRuleAntecedent();
  dingin_kencang_basah_7->joinWithAND(dingin_kencang, basah);
  
  FuzzyRule *fuzzyRule07 = new FuzzyRule(7, dingin_kencang_basah_7, hujan_lebat);
  fuzzy->addFuzzyRule(fuzzyRule07);

  // Rule 8
  
  FuzzyRuleAntecedent *dingin_kencang_sedang_8 = new FuzzyRuleAntecedent();
  dingin_kencang_sedang_8->joinWithAND(dingin_kencang, sedang);
  
  FuzzyRule *fuzzyRule08 = new FuzzyRule(8, dingin_kencang_sedang_8, hujan_lebat);
  fuzzy->addFuzzyRule(fuzzyRule08);

  // Rule 9
  
  FuzzyRuleAntecedent *dingin_kencang_kering_9 = new FuzzyRuleAntecedent();
  dingin_kencang_kering_9->joinWithAND(dingin_kencang, kering);
  
  FuzzyRule *fuzzyRule09 = new FuzzyRule(9, dingin_kencang_kering_9, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule09);

  // Rule 10
  
  FuzzyRuleAntecedent *normal_pelan_basah_10 = new FuzzyRuleAntecedent();
  normal_pelan_basah_10->joinWithAND(normal_pelan, basah);
  
  FuzzyRule *fuzzyRule10 = new FuzzyRule(10, normal_pelan_basah_10, hujan_ringan);
  fuzzy->addFuzzyRule(fuzzyRule10);

  // Rule 11
  
  FuzzyRuleAntecedent *normal_pelan_sedang_11 = new FuzzyRuleAntecedent();
  normal_pelan_sedang_11->joinWithAND(normal_pelan, sedang);

  FuzzyRule *fuzzyRule11 = new FuzzyRule(11, normal_pelan_sedang_11, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule11);

  // Rule 12
  
  FuzzyRuleAntecedent *normal_pelan_kering_12 = new FuzzyRuleAntecedent();
  normal_pelan_kering_12->joinWithAND(normal_pelan, kering);
  
  FuzzyRule *fuzzyRule12 = new FuzzyRule(12, normal_pelan_kering_12, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule12);

  // Rule 13
  
  FuzzyRuleAntecedent *normal_sedang_basah_13 = new FuzzyRuleAntecedent();
  normal_sedang_basah_13->joinWithAND(normal_sedang, basah);
  
  FuzzyRule *fuzzyRule13 = new FuzzyRule(13, normal_sedang_basah_13, hujan_sedang);
  fuzzy->addFuzzyRule(fuzzyRule13);

  // Rule 14
  
  FuzzyRuleAntecedent *normal_sedang_sedang_14 = new FuzzyRuleAntecedent();
  normal_sedang_sedang_14->joinWithAND(normal_sedang, sedang);
  
  FuzzyRule *fuzzyRule14 = new FuzzyRule(14, normal_sedang_sedang_14, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule14);

  // Rule 15
  
  FuzzyRuleAntecedent *normal_sedang_kering_15 = new FuzzyRuleAntecedent();
  normal_sedang_kering_15->joinWithAND(normal_sedang, kering);
  
  FuzzyRule *fuzzyRule15 = new FuzzyRule(15, normal_sedang_kering_15, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule15);

  // Rule 16
  
  FuzzyRuleAntecedent *normal_kencang_basah_16 = new FuzzyRuleAntecedent();
  normal_kencang_basah_16->joinWithAND(normal_kencang, basah);
  
  FuzzyRule *fuzzyRule16 = new FuzzyRule(16, normal_kencang_basah_16, hujan_lebat);
  fuzzy->addFuzzyRule(fuzzyRule16);

  // Rule 17
  
  FuzzyRuleAntecedent *normal_kencang_sedang_17 = new FuzzyRuleAntecedent();
  normal_kencang_sedang_17->joinWithAND(normal_kencang, sedang);

  FuzzyRule *fuzzyRule17 = new FuzzyRule(17, normal_kencang_sedang_17, hujan_sedang);
  fuzzy->addFuzzyRule(fuzzyRule17);

  // Rule 18
  
  FuzzyRuleAntecedent *normal_kencang_kering_18 = new FuzzyRuleAntecedent();
  normal_kencang_kering_18->joinWithAND(normal_kencang, kering);
 
  FuzzyRule *fuzzyRule18 = new FuzzyRule(18, normal_kencang_kering_18, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule18);

  // Rule 19
  
  FuzzyRuleAntecedent *panas_pelan_basah_19 = new FuzzyRuleAntecedent();
  panas_pelan_basah_19->joinWithAND(panas_pelan, basah);
  
  FuzzyRule *fuzzyRule19 = new FuzzyRule(19, panas_pelan_basah_19, hujan_sedang);
  fuzzy->addFuzzyRule(fuzzyRule19);

  // Rule 20
  
  FuzzyRuleAntecedent *panas_pelan_sedang_20 = new FuzzyRuleAntecedent();
  panas_pelan_sedang_20->joinWithAND(panas_pelan, sedang);
  
  FuzzyRule *fuzzyRule20 = new FuzzyRule(20, panas_pelan_sedang_20, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule20);

  // Rule 21
  
  FuzzyRuleAntecedent *panas_pelan_kering_21 = new FuzzyRuleAntecedent();
  panas_pelan_kering_21->joinWithAND(panas_pelan, kering);
  
  FuzzyRule *fuzzyRule21 = new FuzzyRule(21, panas_pelan_kering_21, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule21);

  // Rule 22
  
  FuzzyRuleAntecedent *panas_sedang_basah_22 = new FuzzyRuleAntecedent();
  panas_sedang_basah_22->joinWithAND(panas_sedang, basah);
  
  FuzzyRule *fuzzyRule22 = new FuzzyRule(22, panas_sedang_basah_22, hujan_ringan);
  fuzzy->addFuzzyRule(fuzzyRule22);

  // Rule 23
    
  FuzzyRuleAntecedent *panas_sedang_sedang_23 = new FuzzyRuleAntecedent();
  panas_sedang_sedang_23->joinWithAND(panas_sedang, sedang);
  
  FuzzyRule *fuzzyRule23 = new FuzzyRule(23, panas_sedang_sedang_23, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule23);

  // Rule 24
  
  FuzzyRuleAntecedent *panas_sedang_kering_24 = new FuzzyRuleAntecedent();
  panas_sedang_kering_24->joinWithAND(panas_sedang, kering);
  
  FuzzyRule *fuzzyRule24 = new FuzzyRule(24, panas_sedang_kering_24, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule24);

  // Rule 25
  
  FuzzyRuleAntecedent *panas_kencang_basah_25 = new FuzzyRuleAntecedent();
  panas_kencang_basah_25->joinWithAND(panas_kencang, basah);
  
  FuzzyRule *fuzzyRule25 = new FuzzyRule(25, panas_kencang_basah_25, hujan_lebat);
  fuzzy->addFuzzyRule(fuzzyRule25);

  // Rule 26
  
  FuzzyRuleAntecedent *panas_kencang_sedang_26 = new FuzzyRuleAntecedent();
  panas_kencang_sedang_26->joinWithAND(panas_kencang, sedang);
  
  FuzzyRule *fuzzyRule26 = new FuzzyRule(26, panas_kencang_sedang_26, hujan_ringan);
  fuzzy->addFuzzyRule(fuzzyRule26);

  // Rule 27
  
  FuzzyRuleAntecedent *panas_kencang_kering_27 = new FuzzyRuleAntecedent();
  panas_kencang_kering_27->joinWithAND(panas_kencang, kering);
  
  FuzzyRule *fuzzyRule27 = new FuzzyRule(27, panas_kencang_kering_27, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule27);  
}

void setup()
{
  Serial.begin(9600);
  randomSeed(analogRead(0));

  inisialisasiFuzzyInputOutput();
  inisialisasiFuzzyRule();
}

void loop()
{
  int input1 = 20;
  int input2 = 7;
  int input3 = 75;
  Serial.print("Input: ");
  Serial.print(input1);
  Serial.print(", ");
  Serial.print(input2);
  Serial.print(", ");
  Serial.print(input3);
  Serial.println("");

  fuzzy->setInput(1, input1);
  fuzzy->setInput(2, input2);
  fuzzy->setInput(3, input3);
  fuzzy->fuzzify();
  float output = fuzzy->defuzzify(1);
  Serial.print("Result: ");
  Serial.println(output);

  delay(5000);
}
