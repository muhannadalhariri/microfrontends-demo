cd LookupsService && ./mvnw -ntp -Pprod verify jib:dockerBuild && cd .. &&
cd FinanceService && ./mvnw -ntp -Pprod verify jib:dockerBuild && cd .. &&
cd TransferService && ./mvnw -ntp -Pprod verify jib:dockerBuild && cd ..