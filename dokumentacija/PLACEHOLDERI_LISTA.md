# Lista Placeholdera - ODBIJA SE NSP

## Redosled placeholdera u dokumentu:

1. `«БРОЈ_ПРЕДМЕТА»`
2. `«ДАТУМ_ДОНОШЕЊА_РЕШЕЊА»`
3. `«в.д.»` ← OPCIONO (briše se ako je vrsilacDuznosti=false)
4. `«БРОЈ_ОВЛАШЋЕЊА»`
5. `«ДАТУМ_ОВЛАШЋЕЊА»`
6. `«ИМЕ_И_ПРЕЗИМЕ_ОВЛАШЋЕНОГ_ЛИЦА»`
7. `«ИМЕ_И_ПРЕЗИМЕ_ПОДНОСИОЦА»`
8. `«МЕСТО_СТАНОВАЊА»`
9. `«УЛИЦА»`
10. `«БРОЈ_УЛИЦЕ»`
11. `«ИМЕ_И_ПРЕЗИМЕ_ПОДНОСИОЦА»` (ponovo)
12. `«ЈМБГ»`
13. `«МЕСТО»`
14. `«УЛИЦА_И_БРОЈ»`
15. `«ГРАД_ОПШТИНА»`
16. `«ПТТ_БРОЈ»`
17. `«ДАТУМ_ПОДНОШЕЊА»`
18. `«ИМЕ_И_ПРЕЗИМЕ_ПОДНОСИОЦА»` (ponovo)
19. `«ЈМБГ»` (ponovo)
20. `«МЕСТО»` (ponovo)
21. `«УЛИЦА»` (ponovo)
22. `«БРОЈ_УЛИЦЕ»` (ponovo)
23. `«ОСТВАРЕНОГ_ПРАВА_НА»` ← IZBOR (новчану социјалну помоћ / увећану новчану социјалну помоћ / дечији додатaк / увећани додатак за помоћ и негу другог лица)
24. `«БРОЈ_ЧЛАНОВА_ДОМАЋИНСТВА»`
25. `«СЛУЖБЕНИМ_ПУТЕМ_ТЕКСТ»` ← OPCIONO (ceo paragraf se briše ako je pribavljaDokumentacijuSluzbeno=false)
26. `«ОСНОВ_ПРАВА_ПУНИ_ТЕКСТ»` ← IZBOR SA NEGACIJOM (на новчану социјалну помоћ / није корисник права на...)
27. `«ДОДАТАК_ЗА_ПОМОЋ_ТЕКСТ»` ← OPCIONO (ceo paragraf se briše ako je dodatakZaPomocOdnosiSe=false)
28. `«в.д.»` (ponovo) ← OPCIONO
29. `«с.р.»` ← OPCIONO (briše se ako je sopstveneRuke=false)

---

## Mapiranje DTO → Placeholder:

| DTO polje | Placeholder |
|-----------|-------------|
| `brojPredmeta` | `«БРОЈ_ПРЕДМЕТА»` |
| `datumDonosenja` | `«ДАТУМ_ДОНОШЕЊА_РЕШЕЊА»` |
| `brojOvlascenja` | `«БРОЈ_ОВЛАШЋЕЊА»` |
| `datumOvlascenja` | `«ДАТУМ_ОВЛАШЋЕЊА»` |
| `imeIPrezimeOvlascenog` | `«ИМЕ_И_ПРЕЗИМЕ_ОВЛАШЋЕНОГ_ЛИЦА»` |
| `imeIPrezimePodnosioca` | `«ИМЕ_И_ПРЕЗИМЕ_ПОДНОСИОЦА»` |
| `mestoStanovanja` | `«МЕСТО_СТАНОВАЊА»`, `«МЕСТО»` |
| `ulica` | `«УЛИЦА»` |
| `brojStana` | `«БРОЈ_УЛИЦЕ»` |
| `ulica + brojStana` | `«УЛИЦА_И_БРОЈ»` |
| `grad + opstina` | `«ГРАД_ОПШТИНА»` |
| `pttBroj` | `«ПТТ_БРОЈ»` |
| `jmbg` | `«ЈМБГ»` |
| `datumPodnosenja` | `«ДАТУМ_ПОДНОШЕЊА»` |
| `brojClanovaDomacinstava` | `«БРОЈ_ЧЛАНОВА_ДОМАЋИНСТВА»` |
| `osnovPrava` (НСП/УНСП/ДД/УДДНЛ) | `«ОСТВАРЕНОГ_ПРАВА_НА»` |
| `osnovPrava` (pun tekst) | `«ОСНОВ_ПРАВА_ПУНИ_ТЕКСТ»` |
| `vrsilacDuznosti` | `«в.д.»` (briše se ako false) |
| `sopstveneRuke` | `«с.р.»` (briše se ako false) |
| `pribavljaDokumentacijuSluzbeno` | `«СЛУЖБЕНИМ_ПУТЕМ_ТЕКСТ»` (briše ceo paragraf ako false) |
| `dodatakZaPomocOdnosiSe` | `«ДОДАТАК_ЗА_ПОМОЋ_ТЕКСТ»` (briše ceo paragraf ako false) |

