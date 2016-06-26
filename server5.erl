-module(server5).
-export([start/0]).


sleep(T) ->
    receive
    after T ->
       true
    end. 
sleep2(T) ->
    L =float_to_list(T,[{decimals, 0}]),
    X = list_to_integer(L),
    receive
    after X*1000 ->
       true
    end.     

start()->
   spawn(fun()-> start_server(5000),
        sleep(infinity)
         end).

start_server(Porta) ->
	{ok, Listen} = gen_tcp:listen(Porta,[{packet,line},{reuseaddr,true},{active,true}]),
	GestorLogin = spawn(fun() -> gestorlogin(maps:new()) end),
  GestorTaxis = spawn(fun() -> gestortaxis(maps:new()) end),
  GestorDb = spawn(fun() -> gestordb(maps:new()) end),
  GestorOk = spawn(fun() -> gestorok(maps:new()) end),
  register(gestordb,GestorDb),
  register(gestortaxis,GestorTaxis),
	register(gLogin,GestorLogin),
  register(gestorok,GestorOk),
	spawn(fun() -> new_user(Listen,GestorLogin, GestorTaxis,GestorDb,GestorOk) end),
	io:format("Servidor iniciado!~n").

new_user(Listen,GestorLogin, GestorTaxis, GestorDb,GestorOk)->
      {ok,Socket} = gen_tcp:accept(Listen),
      inet:setopts(Socket, [{packet,line},{nodelay,true},{active,true}]),
      Pid = spawn(fun() -> clientProcess(Socket,GestorLogin, GestorTaxis,GestorDb,GestorOk) end),
      gen_tcp:controlling_process(Socket,Pid),
      new_user(Listen,GestorLogin, GestorTaxis,GestorDb,GestorOk),
      io:format("Pedido a processar!~n").

clientProcess(Socket,GestorLogin, GestorTaxis,GestorDb,GestorOk)->
    io:format("Pedido recebido~n"),
  inet:setopts(Socket,[{active,true}]),
   receive 
      {tcp, Socket, Data} -> 
      	io:format("Servidor recebeu o seguinte pedido = ~p~n",[Data]),
         case converter(removeEOL(Data)) of  
         {login, [Username,Password]} -> 
               %io:format("Login a fazer"), 
               GestorLogin ! {login,Username,Password,self()},
                  receive  
                     {loggedIn} -> 
                        gen_tcp:send(Socket,"Utilizador_entrou\n");
                     {invalid1} -> 
                        gen_tcp:send(Socket,"Password_errada\n");
                     {invalid2} ->      
                        gen_tcp:send(Socket,"Utilizador_nao_existe\n")
                  end,
                  clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);

         {logout, [Username]} -> 
                GestorLogin ! {logout,Username,self()},
                  receive
                      {loggedOut} -> gen_tcp:send(Socket,"Utilizador_saiu\n")
                  end,
                  clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);

         {online} -> 
                GestorLogin ! {listOnlineUsers, self()},
                  receive
                      {L2} -> gen_tcp:send(Socket,"Utilizadores_online_recolhidos\n")
                  end,
                  clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);

         {createAccount,[Username,Password]} ->
                GestorLogin ! {createAccount,Username,Password,self()},
                  receive 
                      {user_exists} -> gen_tcp:send(Socket,"Utilizador_ja_existe\n");
                      {basicAccountCreated} -> gen_tcp:send(Socket, "User_Created\n")
                  end, 
                  clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);

         {createAccountDriver,[Username,Password, Matricula]} ->       
                GestorLogin ! {createAccountDriver,Username,Password,self()},
                  receive
                     {user_exists} -> gen_tcp:send(Socket, "Utilizador_Condutor_ja_existe\n");
                     {driverAccountCreated} -> 
                        GestorTaxis ! {addunavailabletaxi,Username,Matricula,self()},
                          receive
                            {ok} -> gen_tcp:send(Socket, "Driver_Created\n")
                          end
                  end,
                  clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);

         {closeAccount,[Username,Password]} -> 
               GestorLogin ! {closeAccount,Username,Password,self()},
                  receive
                     {accountClosed} -> gen_tcp:send(Socket, "Conta_Removida\n");
                     {invalid} -> gen_tcp:send(Socket, "Username_nao_existe\n")

                  end,
                  clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);
        {normalToDriver,[Username,Matricula]} ->
                GestorLogin ! {normalToDriver,Username,Matricula,self()},
                  receive
                      {ok} -> 
                        GestorTaxis ! {addunavailabletaxi,Username,Matricula,self()},
                          receive
                            {ok} -> gen_tcp:send(Socket, "TaxiAdicionado_normalToDriver\n")
                          end
                  end,
                  clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);
        {availabletaxi,[Username,X,Y]} ->
                GestorDb ! {addCoord, Username, X, Y,self()},
                  receive
                      {coords_updated} -> 
                          GestorTaxis ! {availabletaxi,Username,X,Y, self()},
                            receive
                              {ok} -> io:format("Taxi disponivel\n")
                                     
                                      

                            end
                  end,
                  clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);
       
        {callTaxi, [Username,X,Y,X1,Y1]} ->
               
               GestorDb ! {addCoord,Username, X, Y, self()},
               receive
                    {coords_updated} -> 
                            io:format("Coordenadas cliente\n"),
                            GestorTaxis ! {callTaxi,Username,X,Y,GestorDb,GestorTaxis,self()},
                    receive
                      {taxi_disponivel, Taxista,Tempo} ->
                              io:format("Taxi disponivel\n"),
                               GestorTaxis ! {atualizarflagn,Taxista},

                               L = float_to_list(Tempo,[{decimals, 2}]),
                               gen_tcp:send(Socket, "Taxi_a_caminho_das_coordenadas " ++ X ++ "_" ++ Y ++"_para_levar_nas_coordenadas_" ++ X1 ++ Y1 ++ "_demora_"++ L ++ "\n"),
                               GestorLogin ! {getPid,Taxista,self()},
                               receive
                                    {taxiPid,P4} -> 
                                           P4 ! {novocliente, X,Y,X1,Y1,L,self()}
                                           
                                
                              end,

                               Time = time(),
                               GestorDb ! {addtime, Username, Time},
                               
                               Distancia = spawn(fun() -> 
                                                   O = list_to_float(X),
                                                   M = list_to_float(X1),
                                                   Q = list_to_float(Y),
                                                   R = list_to_float(Y1),
                                                   T = M - O,
                                                   V = R - Q,
                                                   S = 2 * (abs(T)+abs(V)),
                                                   U = preco(S,1),
                                                  GestorOk ! {adicionar,Username,Taxista,Tempo,S,U},
                                                  sleep2(Tempo),
                                                  %sleep(5000),
                                                  GestorOk !  {atualizarflagt,Username,Taxista,U},
                                                  sleep2(S),                           
                                                  GestorOk ! {atualizarflagd,Username,Taxista,S}
                               end),
                               register(dist,Distancia)
                       
                end
              end,
                clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);

          {cancelUser,[Username]} ->
                                            
                                          exit(whereis(dist),kill),
                                          Time2 = time(),
                                          GestorDb ! {sabertempo,Username,self()},
                                            receive
                                                  {stempo,{P3,P4,P5}} -> Time = {P3,P4,P5}
                                            end,

                                           Time3 = calcTempo(Time2,Time),
                                           Preco = preco(Time3,0),
                                           W = float_to_list(Preco),
                                           U = integer_to_list(Time3),
                                           X = Preco/2,
                                           Z = float_to_list(X),
                                           %L = list_to_float(W++".0"),
                                           G = list_to_float(U++".0"),
                                           GestorOk ! {atualizarflagc, Username,Preco,G,self()},
                                          receive
                                             {pagarmetade} -> gen_tcp:send(Socket,"canceled  demorou_" ++ U ++ "_o_preco_a_pagar_" ++ Z ++ "\n");
                                             {pagarinteiro} -> gen_tcp:send(Socket,"canceled  demorou_" ++ U ++ "_o_preco_a_pagar_" ++ W ++ "\n")
                                          end,
                                          clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);
          {cancelDriver,[Taxista]} ->   
                                      GestorOk ! {verflagtaxi, Taxista,self()}, 
                                      receive
                                            {naocanceladotaxi} -> gen_tcp:send(Socket,"notCanceled\n");
                                            {chegoutaxi,Tempo,Preco} ->
                                                    W = Preco / 2.0,
                                                    Z = float_to_list(W,[{decimals, 2}]),
                                                    U = float_to_list(Tempo,[{decimals, 2}]),
                                                    gen_tcp:send(Socket,"canceled  demorou_" ++ U ++ "_o_preco_a_pagar_" ++ Z ++ "\n"); 
                                           {chegoutaxi2,Tempo,Preco} ->  
                                                    O = float_to_list(Preco,[{decimals, 2}]),
                                                    P = float_to_list(Tempo,[{decimals, 2}]),
                                                    gen_tcp:send(Socket,"canceled  demorou_" ++ P ++ "_o_preco_a_pagar_" ++ O ++ "\n")                 
                                      end,                                 
                                      clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);
        
        {okchegoutaxiDriver,[Taxista]} -> 
                          GestorOk ! {chegouDriver, Taxista,self()},
                          receive
                              {naochegouDriver} -> 
                                    gen_tcp:send(Socket,"notTaxi\n");
                              {chegouDriver,Tempo,Preco} ->      
                                    O = float_to_list(Preco,[{decimals, 2}]),
                                    P = float_to_list(Tempo,[{decimals, 2}]),   
                                    gen_tcp:send(Socket,"taxi tempo_de_viagem_" ++ P ++ "_Preco_"++O++"\n")
                          end,
                          clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);
         {okchegoutaxiUser,[Username]} -> 
                          GestorOk ! {chegouDriver2, Username,self()},

                          receive
                              {naochegouDriver2} -> 
                                    gen_tcp:send(Socket,"notTaxi\n");
                              {chegouDriver2,Tempo,Preco} ->   
                                    O = float_to_list(Preco,[{decimals, 2}]),
                                    P = float_to_list(Tempo,[{decimals, 2}]),   
                                    gen_tcp:send(Socket,"taxi tempo_de_viagem_" ++ P ++ "_Preco_"++O++"\n")
                          end,
                          clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);                     


        
        {okchegoudestinoDriver, [Taxista]} ->
                           GestorOk ! {chegouDestino,Taxista,self()},
                          receive
                              {chegouDestino,Preco} -> 
                                     O = float_to_list(Preco,[{decimals, 2}]),
                                     gen_tcp:send(Socket,"destino Valor_a_pagar_"++ O ++ "\n");
                                    
                              {naochegouDestino} ->      
                                    gen_tcp:send(Socket,"notDestino\n")
                          end,
                          clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);

        {okchegoudestinoUser, [Username]} ->
                           GestorOk ! {chegouDestino2,Username,self()},
                          receive
                              {chegouDestino2,Preco} -> 
                                     O = float_to_list(Preco,[{decimals, 2}]),
                                     gen_tcp:send(Socket,"destino Valor_a_pagar_"++ O ++ "\n");
                                    
                              {naochegouDestino2} ->      
                                    gen_tcp:send(Socket,"notDestino\n")
                          end,
                          clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk)                  
                            



         end;
      {propag,MsgProg} ->
          io:format("Mensagem ~p propagada!!!~n",[MsgProg]),    
          gen_tcp:send(Socket,MsgProg++"\n"),
          clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk);
      { tcp_closed, Socket } -> 
          io:format("tcp-closed~p~n",[Socket]);           
      { tcp_error, Socket } ->
          io:format("tcp-error~n");
      {novocliente, X,Y,X1,Y1,L,Pid} ->
                                 gen_tcp:send(Socket, "Taxi_a_caminho_das_coordenadas " ++ X ++ "_" ++ Y ++"_para_levar_nas_coordenadas_" ++ X1 ++ Y1 ++ "_demora_"++ L ++ "\n"),
                                 clientProcess(Socket,GestorLogin,GestorTaxis,GestorDb,GestorOk)
        
      end.


preco(Tempo,X) ->
  if X == 0 ->
    if Tempo < 60.0 -> 0.0;
    true -> Tempo*2.0
  end;
  true -> Tempo*2.0
end.


calcTempo({H1,M1,S1},{H2,M2,S2}) ->
((H1-H2)*60*60) + ((M1-M2)*60*60) + (S1-S2).

gestorok(G) ->
    receive 
      {adicionar,Username,Taxista,Tempo,Tempod,Preco} -> 
            gestorok(maps:put({Username,Taxista},{0,0,0,0,Tempo,Tempod,Preco},G));
      {atualizarflagt,Username,Taxista,U} ->
            case maps:is_key({Username,Taxista},G) of
              true ->
             {P1,P2,P3,P4,P5,P6,P7} = maps:get({Username,Taxista},G),
              gestorok(maps:update({Username,Taxista},{1,P2,P3,P4,P5,P6,U},G)); 
              false ->
              gestorok(G)
            end;
      {atualizarflagd,Username,Taxista,Tempo} ->
              case maps:is_key({Username,Taxista},G) of
                true -> 
                    {P1,P2,P3,P4,P5,P6,P7} = maps:get({Username,Taxista},G),
                    gestorok(maps:update({Username,Taxista},{P1,P2,1,1,P5,Tempo,P7},G));
                false ->
                    gestorok(G)
              end;      
                
           
     {atualizarflagc,Username,Preco,Tempo,Pid} ->
            L = maps:to_list(G),
            {P1,P2,P3,P4,P5,P6,P7,P8} = proc(L,Username,1),
            if P1 == 0 ->
                Pid ! {pagarmetade},
                gestorok(maps:update({Username,P8},{P1,1,P3,P4,Tempo,P6,Preco},G));
            true ->
                Pid ! {pagarinteiro},
                gestorok(maps:update({Username,P8},{P1,1,P3,P4,P5,Tempo,Preco},G))
            end;       
      {verflagtaxi,Taxista,Pid} ->
            L = maps:to_list(G),
            {P1,P2,P3,P4,P5,P6,P7,P8} = proc(L,Taxista,0),
            if P2 ==  0 ->
                  Pid ! {naocanceladotaxi},
                   gestorok(G); 
            true ->
                  if P1 == 0 -> 
                      Pid ! {chegoutaxi,P5,P7},
                      gestorok(maps:remove({P8,Taxista},G));
                  true -> 
                      Pid ! {chegoutaxi2,P6,P7},
                      gestorok(maps:remove({P8,Taxista},G))
                  end       
            end;
      {chegouDriver,Taxista,Pid} ->
           L = maps:to_list(G),
          {P1,P2,P3,P4,P5,P6,P7,P8} = proc(L,Taxista,0),
           if P1 == 0 ->
                Pid ! {naochegouDriver};
           true -> 
                Pid ! {chegouDriver,P6,P7}
           end,
           gestorok(G);
      {chegouDriver2,Username,Pid} ->
           L = maps:to_list(G),
           {P1,P2,P3,P4,P5,P6,P7,P8} = proc(L,Username,1),
           if P1 == 0 ->
                Pid ! {naochegouDriver2};
           true -> 
                Pid ! {chegouDriver2,P6,P7}
           end,
           gestorok(G);       

      {chegouDestino,Taxista,Pid} -> 
         L = maps:to_list(G),
        {P1,P2,P3,P4,P5,P6,P7,P8} = proc(L,Taxista,0),
        W = integer_to_list(P3),
        V = integer_to_list(P4),
         if P4 == 0 -> 
                Pid ! {naochegouDestino},
                gestorok(G);
         true -> 
              if P3 == 0 ->
                 Pid ! {chegouDestino,P7},
                 gestorok(maps:remove({P8,Taxista},G));
              true ->
                 Pid ! {chegouDestino,P7},
                 gestorok(maps:update({P8,Taxista},{P1,P2,P3,0,P5,P6,P7},G))
              end
          end;


        {chegouDestino2,Username,Pid} -> 
         L = maps:to_list(G),
        {P1,P2,P3,P4,P5,P6,P7,P8} = proc(L,Username,1),
        W = integer_to_list(P3),
        V = integer_to_list(P4),
         if P3 == 0 -> 
                Pid ! {naochegouDestino2},
                gestorok(G);
         true -> 
              if P4 == 0 ->
                 Pid ! {chegouDestino2,P7},
                 gestorok(maps:remove({Username,P8},G));
              true ->
                 Pid ! {chegouDestino2,P7},
                 gestorok(maps:update({Username,P8},{P1,P2,0,P4,P5,P6,P7},G))
              end
          end        
    end.

proc([],X,Y) -> {};
proc([H|T],X,Y) ->
  {{User,Taxista},{P1,P2,P3,P4,P5,P6,P7}} = H,
 if Y == 1 ->
    if X == User ->
          {P1,P2,P3,P4,P5,P6,P7,Taxista};
    true ->
       proc(T,X,Y)
    end;
 true -> 
    if X == Taxista ->
          {P1,P2,P3,P4,P5,P6,P7,User};
    true ->       
        proc(T,X,Y)
    end
end.  



gestorlogin(G) ->
   receive
      {createAccount,Username,Passwd, Pid} ->
            case maps:is_key(Username,G) of
               true -> Pid ! {user_exists}, gestorlogin(G);
               false -> 
                  Pid ! {basicAccountCreated},
                  gestorlogin(maps:put(Username,{Passwd, "Normal", 0,Pid},G))
            end;
      {normalToDriver,Username, Pid} ->
            case maps:is_key(Username,G) of
               true -> 
                  Pid ! {ok},
                  {P1,P2,P3,P4} = maps:get(Username,G),
                  gestorlogin(maps:update(Username,{P1,"Driver",P3,P4},G))
            end;
      {createAccountDriver,Username,Passwd, Pid} ->
            case maps:is_key(Username,G) of
               true -> Pid ! {user_exists}, gestorlogin(G);
               false -> 
                  Pid ! {driverAccountCreated},
                  gestorlogin(maps:put(Username,{Passwd, "Driver", 0,Pid},G))
            end;
      {closeAccount,Username,Passwd,Pid} ->
            case maps:is_key(Username,G) of
               true ->
                  {P1,P2,P3,P4} = maps:get(Username,G),
                  if P1==Passwd ->
                     Pid ! {accountClosed},
                     gestorlogin(maps:remove(Username,G))
                  end;
               false -> Pid ! {invalid}, gestorlogin(G)
            end;
      {login,Username,Passwd,Pid} -> 
            %io:format("Fazendo login no gestor de login\n"),
            case maps:is_key(Username,G) of
               true -> 
                  {P1,P2,P3,P4} = maps:get(Username,G),
                  if P1==Passwd ->
                     Pid ! {loggedIn},
                     gestorlogin(maps:update(Username,{Passwd,P2,1,P4},G));
                  %this true represents the else clause
                  true -> Pid ! {invalid1}, gestorlogin(G)
                  end;
               false -> 
                  Pid ! {invalid2}, 
                  gestorlogin(G)
            end;
      {logout,Username,Pid} ->
            Pid ! {loggedOut},
            {P1,P2,P3,P4} = maps:get(Username,G),
            gestorlogin(maps:update(Username,{P1,P2,0,P4},G));
      {listOnlineUsers,Pid} ->
            L = maps:to_list(G),
            L2 = listOn(L,[]),
            Pid ! {L2},
            gestorlogin(G);
      {getPid,Username,Pid} ->
            {P1,P2,P3,P4} = maps:get(Username,G),
            Pid ! {taxiPid,P4},     
            gestorlogin(G)
   
      

      

      

      
   end.





listOn([],L) -> L;
listOn([H|T],L) ->
   {User,{X1,X2,X3,X4}} = H,
   if X3==1 ->
      listOn(T,[User|L]);
   true -> 
      listOn(T,L)
   end.


removeEOL(String)-> string:substr(String,1,(string:len(String)-1)).

converter(String)->
		%String separada por espaços ex: login bruno abc
        case string:tokens(String," ") of 
                [H|T]->{list_to_atom(H),T};
                []->error
        end.


%ver taxis e matricula na db


%user, x, y
gestordb(G) ->
  
   receive

      {addCoord,Username, X, Y, Pid} ->
                case maps:is_key(Username,G) of
                     true -> 
                          %  io:format("Entrou"),
                           {P1,P2,{P3,P4,P5}} = maps:get(Username,G),
                           Pid ! {coords_updated},
                           gestordb(maps:update(Username,{X,Y,{P3,P4,P5}},G));
                     false -> 
                          %io:format("Nao entrou"),
                           Pid ! {coords_updated},
                          gestordb(maps:put(Username,{X,Y,{0,0,0}},G))
                
             end;   
      {call,Username,GestorDb,L,X,Y,GestorTaxis,Pid} ->
              L2 = maps:to_list(G),
              L3 = procurar(L,L2,[]),
              {Taxista,Tempo} = maisPerto(L3,X,Y),   
              if  {Taxista,Tempo} == {"",0} -> 
                   GestorTaxis ! {callTaxi,Username,X,Y,GestorDb,GestorTaxis,Pid};
              true ->    
                   Pid ! {taxi_disponivel, Taxista,Tempo}
                   %GestorTaxis ! {atualizarflagn,Taxista}
              end,
              gestordb(G);
      {addtime,Username,{H,M,S}} ->
             {P1,P2,{P3,P4,P5}} = maps:get(Username,G),
             gestordb(maps:update(Username,{P1,P2,{H,M,S}},G));
                      
      {sabertempo,Username,Pid} ->
            {P1,P2,{P3,P4,P5}} = maps:get(Username,G),
            Pid ! {stempo,{P3,P4,P5}},
            gestordb(G)

              

   end.

gestortaxis(G) ->
   receive  
        {availabletaxi,Username, X, Y, Pid} ->
            
            case maps:is_key(Username,G) of
               true -> 
                  %P1 matricula, P2 flag se disponivel
                  {P1,P2} = maps:get(Username,G),
                  Pid ! {ok},
                 % io:format("estou aqui\n"),
                  gestortaxis(maps:update(Username,{P1,1},G));
               false -> 
                  Pid ! {ok},
                  gestortaxis(maps:put(Username,{"",1},G))
            end;
        {addunavailabletaxi,Username, Matricula, Pid} ->
            case maps:is_key(Username,G) of
               true -> 
                  %P1 matricula, P2 flag se disponivel
                  {P1,P2} = maps:get(Username,G),
                  Pid ! {ok},
                  gestortaxis(maps:update(Username,{Matricula,0},G));
               false -> 
                  Pid ! {ok},
                  gestortaxis(maps:put(Username,{Matricula,0},G))
            end;
         {callTaxi,Username,X,Y,GestorDb,GestorTaxis,Pid} ->
             L1 = maps:to_list(G),
             L = listAvailableTaxis(L1,[]),
             GestorDb ! {call,Username,GestorDb,L,X,Y,GestorTaxis,Pid},
             gestortaxis(G);
         {atualizarflagn,Username} -> 
             {P1,P2} = maps:get(Username,G), 
             if P2 == 1 ->
              gestortaxis(maps:update(Username,{P1,0},G));
             % io:format("FLAG A 0\n");
             true -> 
              gestortaxis(maps:update(Username,{P1,1},G))
             % io:format("FLAG A 1\n")
             end
 
            


            
      
   end.

procurar([],L,L2) -> L2;
procurar(L,[],L2) -> L2;
procurar([User|T],[H|T2],L2) ->
    {User2,{X1,Y1,{P1,P2,P3}}} = H,
    if User == User2 ->
        procurar(T,[H|T2],[H|L2]);
    true ->
        procurar(T,[H|T2],L2) 
    end.    

maisPerto([],X,Y) -> {"",0};
maisPerto([{User,{X1,Y1,{P1,P2,P3}}}],X,Y) -> 
       O = list_to_float(X),
       M = list_to_float(X1),
       Q = list_to_float(Y),
       R = list_to_float(Y1),
       T = M - O,
       V = R - Q,
       S = 2 * (abs(T)+abs(V)),
      {User,S};

maisPerto([H,I|T],X,Y) ->
    {User,{X1,Y1,{P1,P2,P3}}} = H,
    {User,{X2,Y2,{P1,P2,P3}}} = I,
   % io:format("Estou aqui\n"),
    O = list_to_float(X),
    M = list_to_float(X1),
    P = list_to_float(X2),
    Q = list_to_float(Y),
    R = list_to_float(Y1),
    N = list_to_float(Y2),
    T = M - O,
    V = P - O,
    U = R - Q,
    W = N - Q,
   if abs(T) + abs(U) < abs(V) - abs(W) ->
          maisPerto([H|T],X,Y);
   true -> 
          maisPerto([I|T],X,Y)
   end. 

 

%tempo(X,Y,X1,Y1,L) ->
%    X = abs(X1-X)+abs(Y1-Y),
%    L = 30 * X.






listAvailableTaxis([],L) -> L;
listAvailableTaxis([H|T],L) ->
   {User,{Matricula,Disponivel}} = H,
   if Disponivel==1 ->
      listAvailableTaxis(T,[User|L]);
   true -> 
      listAvailableTaxis(T,L)
   end.





   