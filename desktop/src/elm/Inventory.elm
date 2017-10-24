module Inventory exposing (Model, Msg(NewRowWidth), init, update, view)

import Dict exposing (Dict)
import Html exposing (Html)
import Html.Attributes as Attributes


type alias Model =
    { items : Dict String Int
    , rowWidth : Int
    , itemSize : Int
    , padding : Int
    }


type Msg
    = NewRowWidth Int


init : Int -> Model
init rowWidth =
    { items =
        [ ( "Logs", 10 )
        , ( "Planks", 3 )
        , ( "Axe", 1 )
        , ( "Pickaxe", 1 )
        , ( "Logs2", 10 )
        , ( "Planks2", 3 )
        , ( "Axe2", 1 )
        , ( "Pickaxe2", 1 )
        ]
            |> Dict.fromList
    , rowWidth = rowWidth
    , itemSize = 48
    , padding = 12
    }


update : Msg -> Model -> Model
update msg model =
    case msg of
        NewRowWidth rowWidth ->
            { model | rowWidth = rowWidth }


calculateItemsPerRow : Int -> Int -> Int -> Int
calculateItemsPerRow padding width itemSize =
    (width - 32 + padding) // (itemSize + padding)


calculateRowTotalWidth : Int -> Int -> Int -> Int
calculateRowTotalWidth padding itemSize itemsPerRow =
    ((padding + itemSize) * itemsPerRow) - padding


view : Model -> Html Msg
view model =
    let
        itemsPerRow =
            calculateItemsPerRow model.padding model.rowWidth model.itemSize

        rowTotalWidth =
            calculateRowTotalWidth model.padding model.itemSize itemsPerRow

        renderedItems =
            model.items
                |> Dict.toList
                |> List.map viewItem
                |> split itemsPerRow
                |> List.map (viewItemRow rowTotalWidth)
    in
        Html.div
            [ Attributes.id "inventory" ]
            renderedItems


viewItem : ( String, Int ) -> Html Msg
viewItem ( name, amount ) =
    Html.div
        [ Attributes.class "inventory-item-container" ]
        [ Html.div
            [ Attributes.class "inventory-item" ]
            [ Html.div
                [ Attributes.class "inventory-item-name" ]
                [ Html.text name ]
            , Html.div
                [ Attributes.class "inventory-item-amount" ]
                [ Html.text (toString amount) ]
            ]
        ]


viewItemRow : Int -> List (Html Msg) -> Html Msg
viewItemRow width items =
    Html.div
        [ Attributes.class "inventory-row"
        , Attributes.style
            [ ( "width", (toString width) ++ "px" ) ]
        ]
        items



-- Util


split : Int -> List a -> List (List a)
split i list =
    case List.take i list of
        [] ->
            []

        listHead ->
            listHead :: split i (List.drop i list)
