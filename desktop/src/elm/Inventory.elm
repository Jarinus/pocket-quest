module Inventory exposing (Model, Msg, init, update, view)

import Html exposing (Html)


type alias Model =
    {}


type Msg
    = Nothing


init : Model
init =
    {}


update : Msg -> Model -> Model
update msg model =
    case msg of
        Nothing ->
            init


view : Model -> Html Msg
view model =
    Html.div
        []
        []
