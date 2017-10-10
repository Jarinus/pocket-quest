module PocketQuest exposing (main)

import Html exposing (Html)


type alias Model =
    {}


type Msg
    = Nothing


main : Program Never Model Msg
main =
    Html.program
        { init = init
        , subscriptions = subscriptions
        , update = update
        , view = view
        }


init : ( Model, Cmd Msg )
init =
    {} ! []


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        Nothing ->
            init


view : Model -> Html Msg
view model =
    Html.text "Hello, World!"


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none
