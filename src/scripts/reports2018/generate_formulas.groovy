
// indexes for the lists
int MONTH       = 0
int INITIAL     = 1
int FOLLOW_UP   = 2
int CANCEL_NS   = 3
int SCOLIOSIS   = 4
int DANCER      = 5
int ORTHO       = 6
int POTS        = 7
int CLIENT      = 8
int COUNT       = 9

//                     MNTH     INIT    FLLWUP  CNCL    SCOLI   DANCE   ORTHO   POTS    CLINT   COUNT
List<String> T_BCBS = ['BO',    'BP',   'BQ',   'BR',   'BS',   'BT',   'BU',   'BV',   'BW',   'BX']
List<String> T_UHC  = ['BZ',    'CA',   'CB',   'CC',   'CD',   'CE',   'CF',   'CG',   'CH',   'CI']
List<String> T_TRI  = ['CK',    'CL',   'CM',   'CN',   'CO',   'CP',   'CQ',   'CR',   'CS',   'CT']
List<String> T_MEDI = ['CV',    'CW',   'CX',   'CY',   'CZ',   'DA',   'DB',   'DC',   'DD',   'DE']
List<String> T_HUMN = ['DG',    'DH',   'DI',   'DJ',   'DK',   'DL',   'DM',   'DN',   'DO',   'DP']
List<String> T_CASH = ['DR',    'DS',   'DT',   'DU',   'DV',   'DW',   'DX',   'DY',   'DZ',   'EA']

List<String> N_BCBS = ['EC',    'ED',   'EE',   'EF',   'EG',   'EH',   'EI',   'EJ',   'EK',   'EL']
List<String> N_UHC  = ['EN',    'EO',   'EP',   'EQ',   'ER',   'ES',   'ET',   'EU',   'EV',   'EW']
List<String> N_TRI  = ['EY',    'EZ',   'FA',   'FB',   'FC',   'FD',   'FE',   'FF',   'FG',   'FH']
List<String> N_MEDI = ['FJ',    'FK',   'FL',   'FM',   'FN',   'FO',   'FP',   'FQ',   'FR',   'FS']
List<String> N_HUMN = ['FU',    'FV',   'FW',   'FX',   'FY',   'FZ',   'GA',   'GB',   'GC',   'GD']
List<String> N_CASH = ['GF',    'GG',   'GH',   'GI',   'GJ',   'GK',   'GL',   'GM',   'GN',   'GO']

List<String> A_BCBS = ['A',     'B',    'C',    'D',    'E',    'F',    'G',    'H',    'I',    'J']
List<String> A_UHC  = ['L',     'M',    'N',    'O',    'P',    'Q',    'R',    'S',    'T',    'U']
List<String> A_TRI  = ['W',     'X',    'Y',    'Z',    'AA',   'AB',   'AC',   'AD',   'AE',   'AF']
List<String> A_MEDI = ['AH',    'AI',   'AJ',   'AK',   'AL',   'AM',   'AN',   'AO',   'AP',   'AQ']
List<String> A_HUMN = ['AS',    'AT',   'AU',   'AV',   'AW',   'AX',   'AY',   'AZ',   'BA',   'BB']
List<String> A_CASH = ['BD',    'BE',   'BF',   'BG',   'BH',   'BI',   'BJ',   'BK',   'BL',   'BM']

List<String> TYPE_LIST = ['BCBS', 'UHC', 'Tricare', 'Medicare', 'Humana', 'Cash']
List<List> A_LIST = [A_BCBS, A_UHC, A_TRI, A_MEDI, A_HUMN, A_CASH]
List<List> T_LIST = [T_BCBS, T_UHC, T_TRI, T_MEDI, T_HUMN, T_CASH]
List<List> N_LIST = [N_BCBS, N_UHC, N_TRI, N_MEDI, N_HUMN, N_CASH]

String therapist = 'Ashley'

//for(int typeCounter = 0; typeCounter < TYPE_LIST.size(); typeCounter++) {
//    println "\n${therapist} ${TYPE_LIST[typeCounter]}.."
//    for (int monthCounter = 1; monthCounter <= 12; monthCounter++) {
//        println "=SUMIF(Metrics!\$${A_LIST[typeCounter][MONTH]}\$2:\$${A_LIST[typeCounter][MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_LIST[typeCounter][COUNT]}\$2:\$${A_LIST[typeCounter][COUNT]}\$4000)-SUMIF(Metrics!\$${A_LIST[typeCounter][MONTH]}\$2:\$${A_LIST[typeCounter][MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_LIST[typeCounter][CANCEL_NS]}\$2:\$${A_LIST[typeCounter][CANCEL_NS]}\$4000)"
//    }
//}

//println "\n${therapist} Cancel/NS.."
//for (int monthCounter = 1; monthCounter <= 12; monthCounter++) {
//    println "=SUMIF(Metrics!\$${A_BCBS[MONTH]}\$2:\$${A_BCBS[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_BCBS[CANCEL_NS]}\$2:\$${A_BCBS[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${A_UHC[MONTH]}\$2:\$${A_UHC[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_UHC[CANCEL_NS]}\$2:\$${A_UHC[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${A_TRI[MONTH]}\$2:\$${A_TRI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_TRI[CANCEL_NS]}\$2:\$${A_TRI[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${A_MEDI[MONTH]}\$2:\$${A_MEDI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_MEDI[CANCEL_NS]}\$2:\$${A_MEDI[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${A_HUMN[MONTH]}\$2:\$${A_HUMN[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_HUMN[CANCEL_NS]}\$2:\$${A_HUMN[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${A_CASH[MONTH]}\$2:\$${A_CASH[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_CASH[CANCEL_NS]}\$2:\$${A_CASH[CANCEL_NS]}\$4000)"
//}

//[SCOLIOSIS, DANCER, ORTHO, POTS, CLIENT].each { pt ->
//    String ptLabel = ''
//    if(pt == SCOLIOSIS) { ptLabel = 'Scoliosis'}
//    if(pt == DANCER) { ptLabel = 'Dancer'}
//    if(pt == ORTHO) { ptLabel = 'Ortho'}
//    if(pt == POTS) { ptLabel = 'Pots'}
//    if(pt == CLIENT) { ptLabel = 'Client'}
//
//    println "\n${therapist} ${ptLabel}.."
//    for (int monthCounter = 1; monthCounter <= 12; monthCounter++) {
//        println "=SUMIF(Metrics!\$${A_BCBS[MONTH]}\$2:\$${A_BCBS[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_BCBS[pt]}\$2:\$${A_BCBS[pt]}\$4000)+SUMIF(Metrics!\$${A_UHC[MONTH]}\$2:\$${A_UHC[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_UHC[pt]}\$2:\$${A_UHC[pt]}\$4000)+SUMIF(Metrics!\$${A_TRI[MONTH]}\$2:\$${A_TRI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_TRI[pt]}\$2:\$${A_TRI[pt]}\$4000)+SUMIF(Metrics!\$${A_MEDI[MONTH]}\$2:\$${A_MEDI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_MEDI[pt]}\$2:\$${A_MEDI[pt]}\$4000)+SUMIF(Metrics!\$${A_HUMN[MONTH]}\$2:\$${A_HUMN[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_HUMN[pt]}\$2:\$${A_HUMN[pt]}\$4000)+SUMIF(Metrics!\$${A_CASH[MONTH]}\$2:\$${A_CASH[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${A_CASH[pt]}\$2:\$${A_CASH[pt]}\$4000)"
//    }
//}











therapist = 'Tamara'

//for(int typeCounter = 0; typeCounter < TYPE_LIST.size(); typeCounter++) {
//    println "\n${therapist} ${TYPE_LIST[typeCounter]}.."
//    for (int monthCounter = 1; monthCounter <= 12; monthCounter++) {
//        println "=SUMIF(Metrics!\$${T_LIST[typeCounter][MONTH]}\$2:\$${T_LIST[typeCounter][MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_LIST[typeCounter][COUNT]}\$2:\$${T_LIST[typeCounter][COUNT]}\$4000)-SUMIF(Metrics!\$${T_LIST[typeCounter][MONTH]}\$2:\$${T_LIST[typeCounter][MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_LIST[typeCounter][CANCEL_NS]}\$2:\$${T_LIST[typeCounter][CANCEL_NS]}\$4000)"
//    }
//}

//println "\n${therapist} Cancel/NS.."
//for (int monthCounter = 1; monthCounter <= 12; monthCounter++) {
//    println "=SUMIF(Metrics!\$${T_BCBS[MONTH]}\$2:\$${T_BCBS[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_BCBS[CANCEL_NS]}\$2:\$${T_BCBS[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${T_UHC[MONTH]}\$2:\$${T_UHC[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_UHC[CANCEL_NS]}\$2:\$${T_UHC[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${T_TRI[MONTH]}\$2:\$${T_TRI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_TRI[CANCEL_NS]}\$2:\$${T_TRI[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${T_MEDI[MONTH]}\$2:\$${T_MEDI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_MEDI[CANCEL_NS]}\$2:\$${T_MEDI[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${T_HUMN[MONTH]}\$2:\$${T_HUMN[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_HUMN[CANCEL_NS]}\$2:\$${T_HUMN[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${T_CASH[MONTH]}\$2:\$${T_CASH[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_CASH[CANCEL_NS]}\$2:\$${T_CASH[CANCEL_NS]}\$4000)"
//}

[SCOLIOSIS, DANCER, ORTHO, POTS, CLIENT].each { pt ->
    String ptLabel = ''
    if(pt == SCOLIOSIS) { ptLabel = 'Scoliosis'}
    if(pt == DANCER) { ptLabel = 'Dancer'}
    if(pt == ORTHO) { ptLabel = 'Ortho'}
    if(pt == POTS) { ptLabel = 'Pots'}
    if(pt == CLIENT) { ptLabel = 'Client'}

    println "\n${therapist} ${ptLabel}.."
    for (int monthCounter = 1; monthCounter <= 12; monthCounter++) {
        println "=SUMIF(Metrics!\$${T_BCBS[MONTH]}\$2:\$${T_BCBS[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_BCBS[pt]}\$2:\$${T_BCBS[pt]}\$4000)+SUMIF(Metrics!\$${T_UHC[MONTH]}\$2:\$${T_UHC[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_UHC[pt]}\$2:\$${T_UHC[pt]}\$4000)+SUMIF(Metrics!\$${T_TRI[MONTH]}\$2:\$${T_TRI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_TRI[pt]}\$2:\$${T_TRI[pt]}\$4000)+SUMIF(Metrics!\$${T_MEDI[MONTH]}\$2:\$${T_MEDI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_MEDI[pt]}\$2:\$${T_MEDI[pt]}\$4000)+SUMIF(Metrics!\$${T_HUMN[MONTH]}\$2:\$${T_HUMN[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_HUMN[pt]}\$2:\$${T_HUMN[pt]}\$4000)+SUMIF(Metrics!\$${T_CASH[MONTH]}\$2:\$${T_CASH[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${T_CASH[pt]}\$2:\$${T_CASH[pt]}\$4000)"
    }
}























therapist = 'Noelle'

//for(int typeCounter = 0; typeCounter < TYPE_LIST.size(); typeCounter++) {
//    println "\n${therapist} ${TYPE_LIST[typeCounter]}.."
//    for (int monthCounter = 1; monthCounter <= 12; monthCounter++) {
//        println "=SUMIF(Metrics!\$${N_LIST[typeCounter][MONTH]}\$2:\$${N_LIST[typeCounter][MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_LIST[typeCounter][COUNT]}\$2:\$${N_LIST[typeCounter][COUNT]}\$4000)-SUMIF(Metrics!\$${N_LIST[typeCounter][MONTH]}\$2:\$${N_LIST[typeCounter][MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_LIST[typeCounter][CANCEL_NS]}\$2:\$${N_LIST[typeCounter][CANCEL_NS]}\$4000)"
//    }
//}

//println "\n${therapist} Cancel/NS.."
//for (int monthCounter = 1; monthCounter <= 12; monthCounter++) {
//    println "=SUMIF(Metrics!\$${N_BCBS[MONTH]}\$2:\$${N_BCBS[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_BCBS[CANCEL_NS]}\$2:\$${N_BCBS[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${N_UHC[MONTH]}\$2:\$${N_UHC[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_UHC[CANCEL_NS]}\$2:\$${N_UHC[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${N_TRI[MONTH]}\$2:\$${N_TRI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_TRI[CANCEL_NS]}\$2:\$${N_TRI[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${N_MEDI[MONTH]}\$2:\$${N_MEDI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_MEDI[CANCEL_NS]}\$2:\$${N_MEDI[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${N_HUMN[MONTH]}\$2:\$${N_HUMN[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_HUMN[CANCEL_NS]}\$2:\$${N_HUMN[CANCEL_NS]}\$4000)+SUMIF(Metrics!\$${N_CASH[MONTH]}\$2:\$${N_CASH[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_CASH[CANCEL_NS]}\$2:\$${N_CASH[CANCEL_NS]}\$4000)"
//}



[SCOLIOSIS, DANCER, ORTHO, POTS, CLIENT].each { pt ->
    String ptLabel = ''
    if(pt == SCOLIOSIS) { ptLabel = 'Scoliosis'}
    if(pt == DANCER) { ptLabel = 'Dancer'}
    if(pt == ORTHO) { ptLabel = 'Ortho'}
    if(pt == POTS) { ptLabel = 'Pots'}
    if(pt == CLIENT) { ptLabel = 'Client'}

    println "\n${therapist} ${ptLabel}.."
    for (int monthCounter = 1; monthCounter <= 12; monthCounter++) {
        println "=SUMIF(Metrics!\$${N_BCBS[MONTH]}\$2:\$${N_BCBS[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_BCBS[pt]}\$2:\$${N_BCBS[pt]}\$4000)+SUMIF(Metrics!\$${N_UHC[MONTH]}\$2:\$${N_UHC[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_UHC[pt]}\$2:\$${N_UHC[pt]}\$4000)+SUMIF(Metrics!\$${N_TRI[MONTH]}\$2:\$${N_TRI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_TRI[pt]}\$2:\$${N_TRI[pt]}\$4000)+SUMIF(Metrics!\$${N_MEDI[MONTH]}\$2:\$${N_MEDI[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_MEDI[pt]}\$2:\$${N_MEDI[pt]}\$4000)+SUMIF(Metrics!\$${N_HUMN[MONTH]}\$2:\$${N_HUMN[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_HUMN[pt]}\$2:\$${N_HUMN[pt]}\$4000)+SUMIF(Metrics!\$${N_CASH[MONTH]}\$2:\$${N_CASH[MONTH]}\$4000, \"=${monthCounter}\", Metrics!\$${N_CASH[pt]}\$2:\$${N_CASH[pt]}\$4000)"
    }
}







